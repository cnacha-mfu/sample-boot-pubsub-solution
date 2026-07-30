package th.mfu.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * notification-service. Port 8201. A SUBSCRIBER.
 * <p>
 * Nothing to change in this file - the work of step 2 is in
 * BorrowPlacedListener.
 */
@SpringBootApplication
public class NotificationServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApp.class, args);
    }
}
