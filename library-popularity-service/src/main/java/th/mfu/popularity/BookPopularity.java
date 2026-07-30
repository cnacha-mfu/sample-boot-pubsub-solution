package th.mfu.popularity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * How often one book has been borrowed.
 * <p>
 * Look closely at what this row IS: nobody ever posts a count to this service.
 * The count is DERIVED - it is the result of applying every borrow event, one
 * by one, to the table. Delete the table, replay the events, and the same
 * numbers come back. That idea is called EVENT SOURCING: the events are the
 * truth, and state like this is just a convenient summary of them.
 */
@Entity
@Table(name = "book_popularity")
public class BookPopularity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookTitle;
    private int borrowCount;

    public BookPopularity() {
    }

    public BookPopularity(String bookTitle, int borrowCount) {
        this.bookTitle = bookTitle;
        this.borrowCount = borrowCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }
}
