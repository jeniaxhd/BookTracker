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

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

public abstract class AbstractMySqlTcTest {

    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("booktracker")
            .withUsername("test")
            .withPassword("test");

    protected static HikariDataSource dataSource;
    protected static JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void startDb() throws Exception {
        MYSQL.start();

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(MYSQL.getJdbcUrl());
        cfg.setUsername(MYSQL.getUsername());
        cfg.setPassword(MYSQL.getPassword());
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setMaximumPoolSize(5);
        cfg.setMinimumIdle(1);

        dataSource = new HikariDataSource(cfg);
        jdbcTemplate = new JdbcTemplate(dataSource);

        try (Connection c = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(c, new ClassPathResource("schema.sql"));
        }
    }

    @AfterAll
    static void stopDb() {
        if (dataSource != null) dataSource.close();
        MYSQL.stop();
    }

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                        "WHERE table_schema = DATABASE() AND table_type = 'BASE TABLE'",
                String.class
        );

        for (String t : tables) {
            jdbcTemplate.execute("TRUNCATE TABLE `" + t + "`");
        }

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    protected DataSource ds() {
        return dataSource;
    }

    // ✅ ОЦЕ ГОЛОВНЕ ВИПРАВЛЕННЯ:
    protected static JdbcTemplate jt() {
        return jdbcTemplate;
    }
}
