package th.mfu.popularity;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reacts to the SAME borrow events as the notification service - but does
 * something completely different with them.
 * <p>
 * SOLUTION for step 3.
 * <p>
 * The producer sent each event once. Both subscribers receive it, because they
 * subscribe with two different group ids. That is publish/subscribe: one
 * announcement, any number of listeners, and the producer never knew.
 * <p>
 * The count this class maintains is STATE DERIVED FROM EVENTS. Nobody ever
 * posts a count to this service - the number is what falls out of applying
 * every event, in order, to the table. That is the heart of event sourcing:
 * the events are the truth, and this table is only a convenient summary that
 * can be rebuilt from them at any time. Step 5 of the demo does exactly that.
 */
@Component
public class BorrowPlacedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BorrowPlacedListener.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private BookPopularityRepository popularityRepository;

    /*
     * Same topic as the notification service, DIFFERENT group id. That one
     * difference is why both services get every event. Reuse
     * notification-group here and Kafka would treat the two services as copies
     * of one subscriber, and split the stream between them.
     */
    @KafkaListener(topics = "${app.kafka.topic:borrows}", groupId = "popularity-group")
    public void onBorrowPlaced(ConsumerRecord<String, String> record) throws Exception {
        LOGGER.info("received from topic {}: {}", record.topic(), record.value());

        JsonNode event = objectMapper.readTree(record.value());
        String bookTitle = event.get("bookTitle").asText();

        // Apply the event to the derived state: one more borrow for this book.
        BookPopularity entry = popularityRepository.findByBookTitle(bookTitle);
        if (entry == null) {
            entry = new BookPopularity(bookTitle, 0);
        }
        entry.setBorrowCount(entry.getBorrowCount() + 1);
        popularityRepository.save(entry);
    }
}
