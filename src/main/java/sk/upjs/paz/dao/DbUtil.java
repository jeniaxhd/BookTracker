package sk.upjs.paz.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public final class DbUtil {

    private static HikariDataSource dataSource;
    private static JdbcTemplate jdbcTemplate;

    private DbUtil() {
    }

    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig cfg = new HikariConfig();

            String url = getenv("DB_URL",
                    "jdbc:mysql://localhost:3308/booktracker?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
            String user = getenv("DB_USER", "booktracker_user");
            String pass = getenv("DB_PASS", "BookTrackerYevhenVadym2025");


            cfg.setJdbcUrl(url);
            cfg.setUsername(user);
            cfg.setPassword(pass);

            cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // pool settings (нормальні дефолти)
            cfg.setMaximumPoolSize(Integer.parseInt(getenv("DB_POOL_MAX", "10")));
            cfg.setMinimumIdle(Integer.parseInt(getenv("DB_POOL_MIN", "2")));
            cfg.setPoolName("BookTrackerPool");

            // (опціонально) таймаути
            cfg.setConnectionTimeout(Long.parseLong(getenv("DB_CONN_TIMEOUT_MS", "30000")));
            cfg.setIdleTimeout(Long.parseLong(getenv("DB_IDLE_TIMEOUT_MS", "600000")));
            cfg.setMaxLifetime(Long.parseLong(getenv("DB_MAX_LIFETIME_MS", "1800000")));

            dataSource = new HikariDataSource(cfg);
        }
        return dataSource;
    }

    public static synchronized JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(getDataSource());
        }
        return jdbcTemplate;
    }

    public static synchronized void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
        jdbcTemplate = null;
    }

    private static String getenv(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : v;
    }
}
