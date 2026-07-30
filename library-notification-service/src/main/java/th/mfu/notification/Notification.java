package th.mfu.notification;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * One message to one member: "you borrowed this book".
 * <p>
 * This table belongs to THIS service. The producer has no database at all, and
 * the popularity service has its own. Every service keeps its own state - that
 * was true in the microservice sample and it stays true here.
 */
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String memberName;
    private String bookTitle;
    private String message;

    public Notification() {
    }

    public Notification(String memberName, String bookTitle, String message) {
        this.memberName = memberName;
        this.bookTitle = bookTitle;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
