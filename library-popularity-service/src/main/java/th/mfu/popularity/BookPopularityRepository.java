package th.mfu.popularity;

import org.springframework.data.repository.CrudRepository;

public interface BookPopularityRepository extends CrudRepository<BookPopularity, Long> {

    BookPopularity findByBookTitle(String bookTitle);
}
