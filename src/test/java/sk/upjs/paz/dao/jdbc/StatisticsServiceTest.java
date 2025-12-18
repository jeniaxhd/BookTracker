package sk. upjs. paz. dao. jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import sk.upjs.paz.dao.StatisticsDao;
import sk.upjs.paz.dao.jdbc.StatisticsJdbcDao;
import sk.upjs.paz.dao.tc.AbstractDaoTcTest;
import sk.upjs.paz.service.StatisticsService;
import sk.upjs.paz.service.impl.StatisticsServiceImpl;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sk.upjs.paz.dao.DbUtil.getJdbcTemplate;

public class StatisticsServiceTest extends AbstractDaoTcTest {

    private StatisticsService statisticsService;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = getJdbcTemplate();
        StatisticsDao dao = new StatisticsJdbcDao(jdbcTemplate);
        statisticsService = new StatisticsServiceImpl(dao);

        jdbcTemplate.execute("DELETE FROM readingSession");

        jdbcTemplate.update("""
            INSERT INTO readingSession
            (user_id, book_id, endPage, lastTimeRead, duration)
            VALUES (1, 1, 30, '2025-01-01', 25)
        """);
    }

    @Test
    void summaryTotalPagesIsCorrect() {
        var summary = statisticsService.getSummary(
                1,
                StatisticsService.TimeRange.ALL_TIME
        );
        assertEquals(30, summary.totalPages());
    }
}
