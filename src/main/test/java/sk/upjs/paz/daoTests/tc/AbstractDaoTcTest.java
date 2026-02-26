package sk.upjs.paz.dao.tc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.util.List;

public abstract class AbstractDaoTcTest {

    protected static final MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.36"))
            .withDatabaseName("booktracker")
            .withUsername("test")
            .withPassword("test");

    protected static HikariDataSource ds;
    protected static JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void setup() throws Exception {
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
    static void teardown() {
        if (ds != null) ds.close();
        mysql.stop();
    }

    @BeforeEach
    void clean() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        for (String t : tablesToTruncate()) {
            jdbcTemplate.execute("TRUNCATE TABLE `" + t + "`");
        }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    protected JdbcTemplate jt() {
        return jdbcTemplate;
    }

    protected List<String> tablesToTruncate() {
        return List.of(
                "book_has_author",
                "book_has_genre",
                "review",
                "readingSession",
                "user_has_book",
                "author",
                "genre",
                "country",
                "book",
                "user"
        );
    }
}
