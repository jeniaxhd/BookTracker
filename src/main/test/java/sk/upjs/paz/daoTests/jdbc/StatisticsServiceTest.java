package sk.upjs.paz.dao.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import sk.upjs.paz.dao.StatisticsDao;
import sk.upjs.paz.dao.tc.AbstractDaoTcTest;
import sk.upjs.paz.service.StatisticsService;
import sk.upjs.paz.service.impl.StatisticsServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticsServiceTest extends AbstractDaoTcTest {

    private StatisticsService statisticsService;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = jt();

        StatisticsDao dao = new StatisticsJdbcDao(jdbcTemplate);
        statisticsService = new StatisticsServiceImpl(dao);

        // ✅ мінімальні записи для FK
        jdbcTemplate.update("""
            INSERT INTO `user` (id, name, mail, password_hash)
            VALUES (1, 'Test User', 'test@x.sk', 'hash')
        """);

        jdbcTemplate.update("""
            INSERT INTO `book` (id, title)
            VALUES (1, 'Test Book')
        """);

        jdbcTemplate.update("""
            INSERT INTO `readingSession`
            (user_id, book_id, endPage, lastTimeRead, duration)
            VALUES (1, 1, 30, '2025-01-01', 25)
        """);
    }

    @Test
    void summaryTotalPagesIsCorrect() {
        var summary = statisticsService.getSummary(1, StatisticsService.TimeRange.ALL_TIME);
        assertEquals(30, summary.totalPages());
    }
}
