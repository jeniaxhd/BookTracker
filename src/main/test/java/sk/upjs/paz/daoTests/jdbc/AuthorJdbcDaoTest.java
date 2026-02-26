package sk.upjs.paz.dao.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.testcontainers.containers.MySQLContainer;
import sk.upjs.paz.entity.Author;

import java.sql.Connection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthorJdbcDaoTest {

    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("booktracker")
            .withUsername("test")
            .withPassword("test");

    static HikariDataSource ds;
    static JdbcTemplate jdbcTemplate;

    AuthorJdbcDao dao;

    @BeforeAll
    static void setupAll() throws Exception {
        mysql.start();

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(mysql.getJdbcUrl());
        cfg.setUsername(mysql.getUsername());
        cfg.setPassword(mysql.getPassword());
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");

        ds = new HikariDataSource(cfg);
        jdbcTemplate = new JdbcTemplate(ds);

        try (Connection c = ds.getConnection()) {
            ScriptUtils.executeSqlScript(c, new ClassPathResource("schema.sql"));
        }
    }

    @AfterAll
    static void teardownAll() {
        if (ds != null) ds.close();
        mysql.stop();
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE book_has_author");
        jdbcTemplate.execute("TRUNCATE TABLE author");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

        dao = new AuthorJdbcDao(jdbcTemplate);
    }

    @Test
    void add_getById_delete() {
        Author a = new Author();
        a.setName("Albert Camus");
        a.setCountry("France");
        a.setBio("bio");

        dao.add(a);
        assertNotNull(a.getId());

        Optional<Author> loaded = dao.getById(a.getId());
        assertTrue(loaded.isPresent());
        assertEquals("Albert Camus", loaded.get().getName());

        dao.delete(a.getId());
        assertTrue(dao.getById(a.getId()).isEmpty());
    }

    @Test
    void update() {
        Author a = new Author();
        a.setName("A");
        a.setCountry("C");
        a.setBio("B");
        dao.add(a);

        a.setName("A2");
        a.setBio("B2");
        dao.update(a);

        Optional<Author> loaded = dao.getById(a.getId());
        assertTrue(loaded.isPresent());
        assertEquals("A2", loaded.get().getName());
        assertEquals("B2", loaded.get().getBio());
    }

    @Test
    void getByName() {
        Author a1 = new Author();
        a1.setName("Fyodor Dostoevsky");
        a1.setCountry("Russia");
        a1.setBio("bio");
        dao.add(a1);

        Author a2 = new Author();
        a2.setName("Friedrich Nietzsche");
        a2.setCountry("Germany");
        a2.setBio("bio");
        dao.add(a2);

        var res = dao.getByName("Fri");
        assertEquals(1, res.size());
        assertEquals("Friedrich Nietzsche", res.getFirst().getName());
    }
}
