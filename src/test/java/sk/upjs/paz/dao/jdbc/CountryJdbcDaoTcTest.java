package sk.upjs.paz.dao.jdbc;

import org.junit.jupiter.api.Test;
import sk.upjs.paz.dao.tc.AbstractMySqlTcTest;
import sk.upjs.paz.entity.Country;

import static org.junit.jupiter.api.Assertions.*;

public class CountryJdbcDaoTcTest extends AbstractMySqlTcTest {

    @Test
    void crud() {
        CountryJdbcDao dao = new CountryJdbcDao(jdbcTemplate);

        dao.add(new Country(null, "Slovakia"));

        Country saved = dao.getByName("Slovakia").orElseThrow();
        assertNotNull(saved.id());

        dao.update(new Country(saved.id(), "Slovak Republic"));

        Country updated = dao.getById(saved.id()).orElseThrow();
        assertEquals("Slovak Republic", updated.name());

        assertEquals(1, dao.getAll().size());

        dao.delete(saved.id());
        assertTrue(dao.getById(saved.id()).isEmpty());
    }
}
