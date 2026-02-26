package sk.upjs.paz.dao.jdbc;

import org.junit.jupiter.api.Test;
import sk.upjs.paz.dao.tc.AbstractMySqlTcTest;
import sk.upjs.paz.entity.Genre;

import static org.junit.jupiter.api.Assertions.*;

public class GenreJdbcDaoTcTest extends AbstractMySqlTcTest {

    @Test
    void crud() {
        GenreJdbcDao dao = new GenreJdbcDao(jdbcTemplate);

        dao.add(new Genre(null, "Sci-Fi"));
        dao.add(new Genre(null, "Drama"));

        assertEquals(2, dao.getAll().size());

        Genre sci = dao.getByName("Sci-Fi").getFirst();
        assertNotNull(sci.id());

        dao.update(new Genre(sci.id(), "Science Fiction"));

        Genre updated = dao.getById(sci.id()).orElseThrow();
        assertEquals("Science Fiction", updated.name());

        dao.delete(updated.id());
        assertTrue(dao.getById(updated.id()).isEmpty());
    }
}
