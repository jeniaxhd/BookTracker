package sk.upjs.paz.dao.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import sk.upjs.paz.dao.StatisticsDao;
import sk.upjs.paz.enums.BookState;

import java.sql.Date;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class StatisticsJdbcDao implements StatisticsDao {

    private final JdbcTemplate jdbcTemplate;

    public StatisticsJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<DailyValue> getDailyPagesRead(long userId, LocalDate fromInclusive, LocalDate toExclusive) {
        String sql = """
                    SELECT day, COALESCE(SUM(pages_read), 0) AS pages_read
                    FROM (
                        SELECT DATE(lastTimeRead) AS day,
                               GREATEST(0, endPage - IFNULL(prev_endPage, 0)) AS pages_read
                        FROM (
                            SELECT endPage,
                                   lastTimeRead,
                                   LAG(endPage) OVER (PARTITION BY book_id ORDER BY lastTimeRead) AS prev_endPage
                            FROM readingSession
                            WHERE user_id = ?
                        ) s
                        WHERE lastTimeRead >= ? AND lastTimeRead < ?
                    ) x
                    GROUP BY day
                    ORDER BY day
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new DailyValue(
                                rs.getDate("day").toLocalDate(),
                                rs.getLong("pages_read")
                        ),
                userId,
                Date.valueOf(fromInclusive),
                Date.valueOf(toExclusive)
        );
    }

    @Override
    public long getTotalPagesRead(long userId, LocalDate fromInclusive, LocalDate toExclusive) {
        String sql = """
                    SELECT COALESCE(SUM(pages_read), 0) AS total_pages
                    FROM (
                        SELECT GREATEST(0, endPage - IFNULL(prev_endPage, 0)) AS pages_read
                        FROM (
                            SELECT endPage,
                                   lastTimeRead,
                                   LAG(endPage) OVER (PARTITION BY book_id ORDER BY lastTimeRead) AS prev_endPage
                            FROM readingSession
                            WHERE user_id = ?
                        ) s
                        WHERE lastTimeRead >= ? AND lastTimeRead < ?
                    ) x
                """;
        Long v = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                userId,
                Date.valueOf(fromInclusive),
                Date.valueOf(toExclusive)
        );
        return v == null ? 0 : v;
    }

    @Override
    public long getActiveDays(long userId, LocalDate fromInclusive, LocalDate toExclusive) {
        String sql = """
                SELECT COUNT(DISTINCT DATE(lastTimeRead))
                FROM readingSession
                WHERE user_id = ?
                  AND lastTimeRead >= ?
                  AND lastTimeRead < ?
                """;
        Long v = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                userId,
                Date.valueOf(fromInclusive),
                Date.valueOf(toExclusive)
        );
        return v == null ? 0 : v;
    }

    @Override
    public double getAverageSessionMinutes(long userId, LocalDate fromInclusive, LocalDate toExclusive) {
        String sql = """
                SELECT AVG(duration)
                FROM readingSession
                WHERE user_id = ?
                  AND lastTimeRead >= ?
                  AND lastTimeRead < ?
                  AND duration IS NOT NULL
                """;
        Double v = jdbcTemplate.queryForObject(
                sql,
                Double.class,
                userId,
                Date.valueOf(fromInclusive),
                Date.valueOf(toExclusive)
        );
        return v == null ? 0.0 : v;
    }

    @Override
    public List<LocalDate> getAllReadingDates(long userId) {
        String sql = """
                SELECT DISTINCT DATE(lastTimeRead) AS d
                FROM readingSession
                WHERE user_id = ?
                ORDER BY d
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getDate("d").toLocalDate(), userId);
    }

    @Override
    public List<DailyValue> getMonthlyPagesRead(long userId, int year) {
        String sql = """
                    SELECT DATE_FORMAT(day, '%Y-%m-01') AS month_start,
                           COALESCE(SUM(pages_read), 0) AS pages_read
                    FROM (
                        SELECT DATE(lastTimeRead) AS day,
                               lastTimeRead,
                               GREATEST(0, endPage - IFNULL(prev_endPage, 0)) AS pages_read
                        FROM (
                            SELECT endPage,
                                   lastTimeRead,
                                   LAG(endPage) OVER (PARTITION BY book_id ORDER BY lastTimeRead) AS prev_endPage
                            FROM readingSession
                            WHERE user_id = ?
                        ) s
                    ) x
                    WHERE YEAR(lastTimeRead) = ?
                    GROUP BY month_start
                    ORDER BY month_start
                """;


        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new DailyValue(
                        rs.getDate("month_start").toLocalDate(),
                        rs.getLong("pages_read")
                ),
                userId,
                year
        );

    }

    @Override
    public Map<BookState, Long> countBooksByState(long userId) {
        String sql = """
                SELECT bookstate, COUNT(*) AS c
                FROM user_has_book
                WHERE user_id = ?
                GROUP BY bookstate
                """;

        Map<BookState, Long> out = new EnumMap<>(BookState.class);
        jdbcTemplate.query(sql, rs -> {
            String s = rs.getString("bookstate");
            long c = rs.getLong("c");
            if (s != null) {
                try {
                    out.put(BookState.valueOf(s), c);
                } catch (IllegalArgumentException ignored) {
                    // ignore unknown enum values
                }
            }
        }, userId);
        return out;
    }
}
