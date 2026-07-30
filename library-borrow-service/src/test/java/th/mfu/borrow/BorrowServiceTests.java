package th.mfu.borrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests for step 1: the producer.
 * <p>
 * The broker is not running during a test, so KafkaTemplate is replaced by a
 * mock. The test checks that you HAND the event to it - sending it for real is
 * the live demo.
 * <p>
 * This test class is complete. Do not change it.
 */
@SpringBootTest
@AutoConfigureMockMvc
// Without a broker, KafkaAdmin would retry creating the topic for a minute at
// startup. The template is mocked here, so Kafka auto-configuration can be
// switched off entirely.
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration" })
public class BorrowServiceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Environment environment;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String BORROW_JSON =
            "{\"bookTitle\":\"1984\",\"memberName\":\"Alice Johnson\"}";

    @Test
    public void itHasANameAndAPort() {
        assertEquals("library-borrow-service",
                environment.getProperty("spring.application.name"));
        assertEquals("8200", environment.getProperty("server.port"));
    }

    @Test
    public void theTopicIsCalledBorrows() {
        assertEquals("borrows", environment.getProperty("app.kafka.topic"));
    }

    @Test
    public void placingABorrowAnswers201() throws Exception {
        mockMvc.perform(post("/borrows")
                .contentType(MediaType.APPLICATION_JSON).content(BORROW_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void placingABorrowPublishesTheEvent() throws Exception {
        // Step 1. The event must reach the topic, with the body inside it.
        mockMvc.perform(post("/borrows")
                .contentType(MediaType.APPLICATION_JSON).content(BORROW_JSON))
                .andExpect(status().isCreated());

        verify(kafkaTemplate).send(eq("borrows"), contains("1984"));
    }
}
