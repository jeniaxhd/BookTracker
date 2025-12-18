package sk.upjs.paz.dao;

import sk.upjs.paz.enums.BookState;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Read-only DAO for aggregated statistics.
 *
 * <p>Implementation uses MySQL 8+ window functions (LAG) to derive
 * "pages read" from {@code readingSession.endPage} deltas.</p>
 */
public interface StatisticsDao {

    record DailyValue(LocalDate day, long value) {}

    /**
     * Daily pages read within [fromInclusive, toExclusive).
     */
    List<DailyValue> getDailyPagesRead(long userId, LocalDate fromInclusive, LocalDate toExclusive);

    /**
     * Total pages read within [fromInclusive, toExclusive).
     */
    long getTotalPagesRead(long userId, LocalDate fromInclusive, LocalDate toExclusive);

    /**
     * Distinct reading days within [fromInclusive, toExclusive).
     */
    long getActiveDays(long userId, LocalDate fromInclusive, LocalDate toExclusive);

    /**
     * Average session duration (minutes) within [fromInclusive, toExclusive).
     */
    double getAverageSessionMinutes(long userId, LocalDate fromInclusive, LocalDate toExclusive);

    /**
     * Returns distinct reading dates for the user (all time), ordered ascending.
     */
    List<LocalDate> getAllReadingDates(long userId);

    /**
     * Pages read per month for the given year (month = 1..12).
     */
    List<DailyValue> getMonthlyPagesRead(long userId, int year);

    /**
     * Counts user's books by state based on {@code user_has_book}.
     */
    Map<BookState, Long> countBooksByState(long userId);
}
