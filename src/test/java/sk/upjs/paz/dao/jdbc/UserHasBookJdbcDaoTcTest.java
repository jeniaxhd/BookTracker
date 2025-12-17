package sk.upjs.paz.dao.jdbc;

import org.junit.jupiter.api.Test;
import sk.upjs.paz.dao.tc.AbstractMySqlTcTest;
import sk.upjs.paz.dao.tc.TestData;
import sk.upjs.paz.enums.BookState;

import static org.junit.jupiter.api.Assertions.*;

public class UserHasBookJdbcDaoTcTest extends AbstractMySqlTcTest {

    @Test
    void upsert_exists_getState_list_remove() {
        long userId = TestData.insertUser(jdbcTemplate, "U", "u@example.com", "h", 0);
        long bookId = TestData.insertBook(jdbcTemplate, "B", 2020, 100);

        UserHasBookJdbcDao dao = new UserHasBookJdbcDao(jdbcTemplate);

        dao.upsert(userId, bookId, BookState.READING);

        assertTrue(dao.exists(userId, bookId));
        assertEquals(BookState.READING, dao.getState(userId, bookId).orElseThrow());

        assertEquals(1, dao.listByUser(userId).size());

        dao.upsert(userId, bookId, BookState.FINISHED);
        assertEquals(BookState.FINISHED, dao.getState(userId, bookId).orElseThrow());

        dao.remove(userId, bookId);
        assertFalse(dao.exists(userId, bookId));
    }
}
