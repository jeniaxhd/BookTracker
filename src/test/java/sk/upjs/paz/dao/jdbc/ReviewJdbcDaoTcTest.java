package sk.upjs.paz.dao.jdbc;

import org.junit.jupiter.api.Test;
import sk.upjs.paz.dao.tc.AbstractMySqlTcTest;
import sk.upjs.paz.dao.tc.TestData;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.Review;
import sk.upjs.paz.entity.User;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewJdbcDaoTcTest extends AbstractMySqlTcTest {

    @Test
    void crud_and_queries() {
        long userId = TestData.insertUser(jdbcTemplate, "U", "u@example.com", "h", 0);
        long bookId = TestData.insertBook(jdbcTemplate, "B", 2020, 100);

        ReviewJdbcDao dao = new ReviewJdbcDao(jdbcTemplate);

        Review r = new Review();
        r.setComment("nice");
        Book b = new Book();
        b.setId(bookId);
        r.setBook(b);
        User u = new User();
        u.setId(userId);
        r.setUser(u);

        dao.add(r);
        assertNotNull(r.getId());

        Review loaded = dao.getById(r.getId()).orElseThrow();

        assertEquals(1, dao.getByBook(bookId).size());
        assertEquals(1, dao.getByUser(userId).size());
        assertTrue(dao.getByUserAndBook(userId, bookId).isPresent());

        r.setComment("updated");
        dao.update(r);

        Review updated = dao.getById(r.getId()).orElseThrow();
        assertEquals("updated", updated.getComment());

        dao.delete(r.getId());
        assertTrue(dao.getById(r.getId()).isEmpty());
    }
}
