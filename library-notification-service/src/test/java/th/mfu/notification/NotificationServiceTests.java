package th.mfu.notification;

import static org.hamcrest.Matchers.containsString;
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
 * Tests for step 2: the first subscriber.
 * <p>
 * The broker is not running during a test, so the listener method is called
 * directly with the same kind of record the broker would deliver. The listener
 * containers are switched off.
 * <p>
 * This test class is complete. Do not change it.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "spring.kafka.listener.auto-startup=false" })
public class NotificationServiceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Environment environment;

    @Autowired
    private BorrowPlacedListener listener;

    @Autowired
    private NotificationRepository repository;

    private static final String BORROW_JSON =
            "{\"bookTitle\":\"1984\",\"memberName\":\"Alice Johnson\"}";

    private ConsumerRecord<String, String> aRecord(String json) {
        return new ConsumerRecord<>("borrows", 0, 0, null, json);
    }

    @Test
    public void itHasANameAndAPort() {
        assertEquals("library-notification-service",
                environment.getProperty("spring.application.name"));
        assertEquals("8201", environment.getProperty("server.port"));
    }

    @Test
    public void theListenerSubscribesWithItsOwnGroup() throws Exception {
        // Step 2. The annotation is what subscribes the method to the topic.
        Method method = BorrowPlacedListener.class.getMethod("onBorrowPlaced", ConsumerRecord.class);
        KafkaListener annotation = AnnotationUtils.findAnnotation(method, KafkaListener.class);

        assertNotNull(annotation, "put @KafkaListener on onBorrowPlaced");
        assertEquals("notification-group", annotation.groupId(),
                "the group id must be notification-group - a group of its own");
    }

    @Test
    public void anEventBecomesANotification() throws Exception {
        listener.onBorrowPlaced(aRecord(BORROW_JSON));

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberName").value("Alice Johnson"))
                .andExpect(jsonPath("$[0].bookTitle").value("1984"))
                .andExpect(jsonPath("$[0].message").value(containsString("1984")));
    }

    @Test
    public void everyEventBecomesItsOwnNotification() throws Exception {
        long before = repository.count();

        listener.onBorrowPlaced(aRecord(BORROW_JSON));
        listener.onBorrowPlaced(aRecord(
                "{\"bookTitle\":\"The Hobbit\",\"memberName\":\"Bob Smith\"}"));

        assertEquals(before + 2, repository.count());
    }
}
