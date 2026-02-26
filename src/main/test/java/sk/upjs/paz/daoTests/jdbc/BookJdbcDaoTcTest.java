package sk.upjs.paz.dao.jdbc;

import org.junit.jupiter.api.Test;
import sk.upjs.paz.dao.tc.AbstractMySqlTcTest;
import sk.upjs.paz.dao.tc.TestData;
import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Book;
import sk.upjs.paz.entity.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookJdbcDaoTcTest extends AbstractMySqlTcTest {

    @Test
    void add_get_update_delete() {
        long a1Id = TestData.insertAuthor(jdbcTemplate, "Author1", "X", "bio", null);
        long a2Id = TestData.insertAuthor(jdbcTemplate, "Author2", "X", "bio", null);
        long g1Id = TestData.insertGenre(jdbcTemplate, "G1");
        long g2Id = TestData.insertGenre(jdbcTemplate, "G2");

        BookJdbcDao dao = new BookJdbcDao(jdbcTemplate);

        Author a1 = new Author();
        a1.setId(a1Id);
        a1.setName("Author1");

        Genre g1 = new Genre(g1Id, "G1");

        Book b = new Book();
        b.setTitle("Book1");
        b.setDescription("D");
        b.setYear(2020);
        b.setPages(111);
        b.setCoverPath(null);
        b.setAuthors(List.of(a1));
        b.setGenre(List.of(g1));

        dao.add(b);
        assertNotNull(b.getId());

        Book loaded = dao.getById(b.getId()).orElseThrow();
        assertEquals("Book1", loaded.getTitle());
        assertEquals(1, loaded.getAuthors().size());
        assertEquals(1, loaded.getGenre().size());

        long userId = TestData.insertUser(jdbcTemplate, "U", "u@example.com", "h", 0);
        TestData.insertReview(jdbcTemplate, userId, b.getId(), 4, "ok");
        TestData.insertReview(jdbcTemplate, userId, b.getId(), 2, "meh");

        Book withRating = dao.getById(b.getId()).orElseThrow();

        Author a2 = new Author();
        a2.setId(a2Id);
        a2.setName("Author2");

        Genre g2 = new Genre(g2Id, "G2");

        b.setTitle("Book1x");
        b.setAuthors(List.of(a2));
        b.setGenre(List.of(g1, g2));

        dao.update(b);

        Book afterUpdate = dao.getById(b.getId()).orElseThrow();
        assertEquals("Book1x", afterUpdate.getTitle());
        assertEquals(1, afterUpdate.getAuthors().size());
        assertEquals("Author2", afterUpdate.getAuthors().getFirst().getName());
        assertEquals(2, afterUpdate.getGenre().size());

        assertEquals(1, dao.getByAuthor(a2).size());
        assertEquals(1, dao.getByGenre(g2).size());
        assertEquals(1, dao.getByGenres(List.of(g1, g2)).size());

        dao.delete(b.getId());
        assertTrue(dao.getById(b.getId()).isEmpty());
    }
}
