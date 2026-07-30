package th.mfu.popularity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Method;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests for step 3: the second subscriber, the one that derives state from the
 * events.
 * <p>
 * This test class is complete. Do not change it.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "spring.kafka.listener.auto-startup=false" })
public class PopularityServiceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Environment environment;

    @Autowired
    private BorrowPlacedListener listener;

    private ConsumerRecord<String, String> borrowOf(String title) {
        return new ConsumerRecord<>("borrows", 0, 0, null,
                "{\"bookTitle\":\"" + title + "\",\"memberName\":\"Alice Johnson\"}");
    }

    @Test
    public void itHasANameAndAPort() {
        assertEquals("library-popularity-service",
                environment.getProperty("spring.application.name"));
        assertEquals("8202", environment.getProperty("server.port"));
    }

    @Test
    public void theListenerSubscribesWithItsOwnGroup() throws Exception {
        Method method = BorrowPlacedListener.class.getMethod("onBorrowPlaced", ConsumerRecord.class);
        KafkaListener annotation = AnnotationUtils.findAnnotation(method, KafkaListener.class);

        assertNotNull(annotation, "put @KafkaListener on onBorrowPlaced");
        assertEquals("popularity-group", annotation.groupId(),
                "the group id must be popularity-group. With the notification "
                + "service's group id the two services would SHARE the stream "
                + "instead of each getting every event");
    }

    @Test
    public void aNewGroupStartsFromTheBeginningOfTheLog() {
        // Step 5 depends on this setting: a brand new group replays the whole
        // topic instead of starting "from now".
        assertEquals("earliest",
                environment.getProperty("spring.kafka.consumer.auto-offset-reset"));
    }

    @Test
    public void borrowingTheSameBookTwiceCountsToTwo() throws Exception {
        listener.onBorrowPlaced(borrowOf("1984"));
        listener.onBorrowPlaced(borrowOf("1984"));
        listener.onBorrowPlaced(borrowOf("The Hobbit"));

        mockMvc.perform(get("/popularity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.bookTitle == '1984')].borrowCount").value(2))
                .andExpect(jsonPath("$.[?(@.bookTitle == 'The Hobbit')].borrowCount").value(1));
    }
}
