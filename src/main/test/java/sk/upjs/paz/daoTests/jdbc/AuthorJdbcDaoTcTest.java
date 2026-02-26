package sk.upjs.paz.dao.jdbc;

import org.junit.jupiter.api.Test;
import sk.upjs.paz.dao.tc.AbstractMySqlTcTest;
import sk.upjs.paz.dao.tc.TestData;
import sk.upjs.paz.entity.Author;
import sk.upjs.paz.entity.Country;

import static org.junit.jupiter.api.Assertions.*;

public class AuthorJdbcDaoTcTest extends AbstractMySqlTcTest {

    @Test
    void crud_and_queries() {
        long countryId = TestData.insertCountry(jdbcTemplate, "France");
        Country france = new Country(countryId, "France");

        AuthorJdbcDao dao = new AuthorJdbcDao(jdbcTemplate);

        Author a = new Author();
        a.setName("Albert Camus");
        a.setCountry("France");
        a.setBio("bio");
        a.setCountryObj(france);

        dao.add(a);
        assertNotNull(a.getId());

        Author loaded = dao.getById(a.getId()).orElseThrow();
        assertEquals("Albert Camus", loaded.getName());

        assertEquals(1, dao.getByName("Camus").size());
        assertEquals(1, dao.getByCountry(france).size());

        a.setBio("bio2");
        dao.update(a);

        Author updated = dao.getById(a.getId()).orElseThrow();
        assertEquals("bio2", updated.getBio());

        dao.delete(a.getId());
        assertTrue(dao.getById(a.getId()).isEmpty());
    }
}
