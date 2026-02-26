package sk.upjs.paz.dao.jdbc;

import org.junit.jupiter.api.Test;
import sk.upjs.paz.dao.tc.AbstractMySqlTcTest;
import sk.upjs.paz.dao.tc.TestData;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.ReadingSession;
import sk.upjs.paz.entity.User;
import sk.upjs.paz.enums.BookState;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReadingSessionJdbcDaoTcTest extends AbstractMySqlTcTest {

    @Test
    void crud_and_state() {
        long userId = TestData.insertUser(jdbcTemplate, "U", "u@example.com", "h", 0);
        long bookId = TestData.insertBook(jdbcTemplate, "B", 2020, 100);

        TestData.upsertUserHasBook(jdbcTemplate, userId, bookId, BookState.NOT_STARTED);

        ReadingSessionJdbcDao dao = new ReadingSessionJdbcDao(jdbcTemplate);

        ReadingSession s = new ReadingSession();
        Book b = new Book();
        b.setId(bookId);
        User u = new User();
        u.setId(userId);
        s.setBook(b);
        s.setUser(u);
        s.setStart(LocalDateTime.now().minusHours(1));
        s.setDuration(30);
        s.setEndPage(25);
        s.setLastTimeRead(LocalDateTime.now());

        dao.add(s);
        assertNotNull(s.getId());

        ReadingSession loaded = dao.getById(s.getId()).orElseThrow();
        assertEquals(30, loaded.getDuration());

        assertEquals(1, dao.getByUser(userId).size());
        assertEquals(1, dao.getByBook(bookId).size());
        assertTrue(dao.getByUserAndBook(userId, bookId).isPresent());

        s.setDuration(40);
        dao.update(s);
        assertEquals(40, dao.getById(s.getId()).orElseThrow().getDuration());

        dao.updateBookState(userId, bookId, BookState.READING);
        assertEquals("READING",
                jdbcTemplate.queryForObject("SELECT bookstate FROM user_has_book WHERE user_id = ? AND book_id = ?",
                        String.class, userId, bookId));

        dao.delete(s.getId());
        assertTrue(dao.getById(s.getId()).isEmpty());
    }
}
