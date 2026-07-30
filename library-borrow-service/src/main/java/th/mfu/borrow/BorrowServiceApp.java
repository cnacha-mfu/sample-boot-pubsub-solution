package th.mfu.borrow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * borrow-service. Port 8200. The PRODUCER.
 * <p>
 * Nothing to change in this file - the work of step 1 is in BorrowController.
 */
@SpringBootApplication
public class BorrowServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(BorrowServiceApp.class, args);
    }
}
