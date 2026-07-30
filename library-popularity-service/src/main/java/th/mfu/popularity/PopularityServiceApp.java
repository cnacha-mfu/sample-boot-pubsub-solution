package th.mfu.popularity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * popularity-service. Port 8202. The SECOND subscriber.
 * <p>
 * Nothing to change in this file - the work of step 3 is in
 * BorrowPlacedListener.
 */
@SpringBootApplication
public class PopularityServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(PopularityServiceApp.class, args);
    }
}
