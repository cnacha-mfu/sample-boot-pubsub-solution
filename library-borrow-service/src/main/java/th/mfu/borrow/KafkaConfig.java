package th.mfu.borrow;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Creates the topic when this service starts, if it does not exist yet.
 * <p>
 * A topic is a named channel inside the broker. Events go IN at one end and
 * every subscribed group reads them OUT at the other. This one is called
 * "borrows", it has 1 partition, and 1 copy - fine for a classroom, never for
 * production.
 * <p>
 * Already finished - nothing to do here.
 */
@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic:borrows}")
    private String topicName;

    @Bean
    public NewTopic borrowsTopic() {
        return new NewTopic(topicName, 1, (short) 1);
    }
}
