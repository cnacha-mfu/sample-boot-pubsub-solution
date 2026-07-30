package th.mfu.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read side: the page at http://localhost:8201/ calls this every two seconds.
 * Already finished - nothing to do here.
 */
@RestController
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/notifications")
    public Iterable<Notification> listNotifications() {
        return notificationRepository.findAll();
    }
}
