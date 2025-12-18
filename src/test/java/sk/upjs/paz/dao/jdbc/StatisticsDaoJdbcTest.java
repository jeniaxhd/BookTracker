package sk. upjs. paz. dao. jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import sk.upjs.paz.dao.StatisticsDao;
import sk.upjs.paz.dao.jdbc.StatisticsJdbcDao;
import sk.upjs.paz.dao.tc.AbstractDaoTcTest;
import sk.upjs.paz.enums.BookState;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static sk.upjs.paz.dao.DbUtil.getJdbcTemplate;

public class StatisticsDaoJdbcTest extends AbstractDaoTcTest {

    private StatisticsDao statisticsDao;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = getJdbcTemplate();
        statisticsDao = new StatisticsJdbcDao(jdbcTemplate);

        jdbcTemplate.execute("DELETE FROM readingSession");
        jdbcTemplate.execute("DELETE FROM user_has_book");

        jdbcTemplate.update("""
            INSERT INTO readingSession
            (user_id, book_id, endPage, lastTimeRead, duration)
            VALUES (1, 1, 20, '2025-01-01', 30)
        """);

        jdbcTemplate.update("""
            INSERT INTO readingSession
            (user_id, book_id, endPage, lastTimeRead, duration)
            VALUES (1, 1, 50, '2025-01-02', 40)
        """);

        jdbcTemplate.update("""
            INSERT INTO user_has_book (user_id, book_id, bookstate)
            VALUES (1, 1, 'FINISHED')
        """);
    }

    @Test
    void totalPagesReadIsCalculatedCorrectly() {
        long pages = statisticsDao.getTotalPagesRead(
                1,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 10)
        );
        assertEquals(50, pages);
    }

    @Test
    void activeDaysAreCalculatedCorrectly() {
        long days = statisticsDao.getActiveDays(
                1,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 10)
        );
        assertEquals(2, days);
    }

    @Test
    void averageSessionMinutesIsCalculated() {
        double avg = statisticsDao.getAverageSessionMinutes(
                1,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 10)
        );
        assertEquals(35.0, avg);
    }

    @Test
    void countBooksByStateWorks() {
        Map<BookState, Long> map = statisticsDao.countBooksByState(1);
        assertEquals(1L, map.get(BookState.FINISHED));
    }
}
