package sk.upjs.paz.dao.jdbc;

import org.junit.jupiter.api.Test;
import sk.upjs.paz.dao.tc.AbstractMySqlTcTest;
import sk.upjs.paz.entity.User;

import static org.junit.jupiter.api.Assertions.*;

public class UserJdbcDaoTcTest extends AbstractMySqlTcTest {

    @Test
    void crud_getByEmail_updateReadBooks() {
        UserJdbcDao dao = new UserJdbcDao(jdbcTemplate);

        User u = new User();
        u.setName("Bogdan");
        u.setEmail("bogdan@example.com");
        u.setPasswordHash("hash");
        u.setReadBooks(0);

        dao.add(u);
        assertNotNull(u.getId());

        User byEmail = dao.getByEmail("bogdan@example.com").orElseThrow();
        assertEquals(u.getId(), byEmail.getId());

        dao.updateReadBooks(u.getId(), 7);
        User afterCount = dao.getById(u.getId()).orElseThrow();
        assertEquals(7, afterCount.getReadBooks());

        u.setName("Bogdan2");
        dao.update(u);

        User afterUpdate = dao.getById(u.getId()).orElseThrow();
        assertEquals("Bogdan2", afterUpdate.getName());

        assertEquals(1, dao.getByName("Bog").size());

        dao.delete(u.getId());
        assertTrue(dao.getById(u.getId()).isEmpty());
    }
}
