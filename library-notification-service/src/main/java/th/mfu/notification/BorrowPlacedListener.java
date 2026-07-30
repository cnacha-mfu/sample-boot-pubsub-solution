package th.mfu.notification;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reacts to borrow events: writes a notification for the member.
 * <p>
 * SOLUTION for step 2.
 * <p>
 * Nobody calls this class - not the producer, not our own controllers. The
 * broker delivers every event on the topic to the method below, because of the
 * annotation. Compare that with the microservice sample, where the caller had
 * to know the callee by name. Here the producer does not know this class
 * exists.
 */
@Component
public class BorrowPlacedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BorrowPlacedListener.class);

    /** Jackson - the same JSON library the whole course has used. */
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private NotificationRepository notificationRepository;

    /*
     * The annotation says two things.
     *
     * topics - WHICH channel to read.
     *
     * groupId - WHO IS READING. Kafka delivers every event once to EACH group.
     * This service and the popularity service use different group ids, so both
     * of them get every event. Two copies of the SAME service would share one
     * group id, and then Kafka would split the stream between them - which is
     * load balancing, the same idea as two copies of book-service last time.
     */
    @KafkaListener(topics = "${app.kafka.topic:borrows}", groupId = "notification-group")
    public void onBorrowPlaced(ConsumerRecord<String, String> record) throws Exception {
        LOGGER.info("received from topic {}: {}", record.topic(), record.value());

        // Turn the event back into data...
        JsonNode event = objectMapper.readTree(record.value());
        String memberName = event.get("memberName").asText();
        String bookTitle = event.get("bookTitle").asText();

        // ...and react to it. This service's reaction is a notification. The
        // popularity service reacts to the same event completely differently.
        Notification notification = new Notification(memberName, bookTitle,
                "Dear " + memberName + ", you borrowed " + bookTitle);
        notificationRepository.save(notification);
    }
}
