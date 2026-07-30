package th.mfu.borrow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Records a borrow - by ANNOUNCING it.
 * <p>
 * SOLUTION for step 1.
 * <p>
 * Compare the one important line with the microservice sample:
 *
 * <pre>
 *   BookDTO book = bookClient.getBook(dto.getBookId());   // call, and WAIT
 *   kafkaTemplate.send(topicName, borrowJson);            // announce, move on
 * </pre>
 *
 * The Feign call needed the other service to be up, to answer in time, and to
 * answer well - remember the two catch blocks. send() needs none of that. It
 * hands the event to the broker and returns. Who reads it, when, and what they
 * do with it is not this service's business any more.
 */
@RestController
@RequestMapping("/borrows")
public class BorrowController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BorrowController.class);

    /**
     * The one Kafka class a producer needs. send(topic, message) hands the
     * message to the broker.
     */
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /** The name of the channel. Every service in this system agrees on it. */
    @Value("${app.kafka.topic:borrows}")
    private String topicName;

    /**
     * POST /borrows with a body like:
     *
     * <pre>
     *   { "bookTitle": "1984", "memberName": "Alice Johnson" }
     * </pre>
     */
    @PostMapping
    public ResponseEntity<String> placeBorrow(@RequestBody String borrowJson) {

        /*
         * One line does the work. Note what it does NOT do: it does not wait
         * for anybody to read the event, and it cannot know who will. Right
         * now two services subscribe; next month it could be five, and this
         * line would not change.
         */
        kafkaTemplate.send(topicName, borrowJson);
        LOGGER.info("published to {}: {}", topicName, borrowJson);

        return new ResponseEntity<>("Borrow placed", HttpStatus.CREATED);
    }
}
