package sk.upjs.paz.dao.jdbc;

import org.junit.jupiter.api.Test;
import sk.upjs.paz.dao.tc.AbstractMySqlTcTest;
import sk.upjs.paz.dao.tc.TestData;
import sk.upjs.paz.enums.BookState;
import sk.upjs.paz.ui.dto.ActiveBookCard;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CurrentlyReadingJdbcDaoTcTest extends AbstractMySqlTcTest {

    @Test
    void listActiveBooks() {
        long userId = TestData.insertUser(jdbcTemplate, "U", "u@example.com", "h", 0);
        long bookId = TestData.insertBook(jdbcTemplate, "The Stranger", 1942, 123);

        long authorId = TestData.insertAuthor(jdbcTemplate, "Albert Camus", "France", "bio", null);
        long genreId = TestData.insertGenre(jdbcTemplate, "Novel");

        TestData.linkBookAuthor(jdbcTemplate, bookId, authorId);
        TestData.linkBookGenre(jdbcTemplate, bookId, genreId);

        TestData.upsertUserHasBook(jdbcTemplate, userId, bookId, BookState.READING);

        TestData.insertReadingSession(jdbcTemplate, userId, bookId, 20, 10);
        TestData.insertReadingSession(jdbcTemplate, userId, bookId, 30, 25);

        CurrentlyReadingJdbcDao dao = new CurrentlyReadingJdbcDao(jdbcTemplate);

        List<ActiveBookCard> cards = dao.listActiveBooks(userId);
        assertEquals(1, cards.size());

        ActiveBookCard c = cards.getFirst();
        assertEquals(bookId, c.bookId());
        assertEquals("The Stranger", c.title());
        assertTrue(c.authorsText().contains("Albert Camus"));
        assertTrue(c.genreName().contains("Novel"));
        assertEquals(123, c.totalPages());
        assertEquals(25, c.currentPage());
        assertEquals(50, c.totalMinutes());
    }
}
