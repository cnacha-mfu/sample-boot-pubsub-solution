package th.mfu.popularity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read side: the page at http://localhost:8202/ calls this every two seconds.
 * Already finished - nothing to do here.
 */
@RestController
public class PopularityController {

    @Autowired
    private BookPopularityRepository popularityRepository;

    @GetMapping("/popularity")
    public Iterable<BookPopularity> listPopularity() {
        return popularityRepository.findAll();
    }
}
