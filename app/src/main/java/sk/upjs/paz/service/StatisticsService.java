package sk.upjs.paz.service;

import sk.upjs.paz.enums.BookState;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatisticsService {

    enum TimeRange {
        LAST_7_DAYS,
        LAST_30_DAYS,
        THIS_YEAR,
        ALL_TIME
    }

    record Streak(long currentDays, long bestDays) {}

    record Summary(long totalPages, double averagePerDay, long bestDayPages) {}

    record YearOverview(long booksFinished, long pagesRead, long activeReadingDays, double avgSessionMinutes) {}

    record DailyPoint(LocalDate day, long value) {}

    Summary getSummary(long userId, TimeRange range);

    List<DailyPoint> getDailyPages(long userId, TimeRange range);

    List<DailyPoint> getMonthlyPagesForYear(long userId, int year);

    Streak getStreak(long userId);

    YearOverview getYearOverview(long userId, int year);

    Map<BookState, Long> countBooksByState(long userId);

}
