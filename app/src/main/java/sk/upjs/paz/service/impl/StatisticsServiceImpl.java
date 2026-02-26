package sk.upjs.paz.service.impl;

import sk.upjs.paz.dao.StatisticsDao;
import sk.upjs.paz.enums.BookState;
import sk.upjs.paz.service.StatisticsService;

import java.time.LocalDate;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsDao statisticsDao;

    public StatisticsServiceImpl(StatisticsDao statisticsDao) {
        this.statisticsDao = statisticsDao;
    }

    private static LocalDate rangeStart(TimeRange range) {
        LocalDate today = LocalDate.now();
        return switch (range) {
            case LAST_7_DAYS -> today.minusDays(7);
            case LAST_30_DAYS -> today.minusDays(30);
            case THIS_YEAR -> LocalDate.of(today.getYear(), 1, 1);
            case ALL_TIME -> LocalDate.of(1970, 1, 1);
        };
    }

    private static LocalDate rangeEndExclusive(TimeRange range) {
        // include today by making end exclusive tomorrow
        return LocalDate.now().plusDays(1);
    }

    @Override
    public Summary getSummary(long userId, TimeRange range) {
        LocalDate from = rangeStart(range);
        LocalDate to = rangeEndExclusive(range);

        long total = statisticsDao.getTotalPagesRead(userId, from, to);
        long activeDays = statisticsDao.getActiveDays(userId, from, to);
        double avgPerDay = activeDays == 0 ? 0.0 : (double) total / (double) activeDays;

        long best = 0;
        for (StatisticsDao.DailyValue dv : statisticsDao.getDailyPagesRead(userId, from, to)) {
            if (dv.value() > best) best = dv.value();
        }
        return new Summary(total, avgPerDay, best);
    }

    @Override
    public List<DailyPoint> getDailyPages(long userId, TimeRange range) {
        LocalDate from = rangeStart(range);
        LocalDate to = rangeEndExclusive(range);
        return statisticsDao.getDailyPagesRead(userId, from, to)
                .stream()
                .map(dv -> new DailyPoint(dv.day(), dv.value()))
                .toList();
    }

    @Override
    public List<DailyPoint> getMonthlyPagesForYear(long userId, int year) {
        return statisticsDao.getMonthlyPagesRead(userId, year)
                .stream()
                .map(dv -> new DailyPoint(dv.day(), dv.value()))
                .toList();
    }

    @Override
    public Streak getStreak(long userId) {
        List<LocalDate> days = statisticsDao.getAllReadingDates(userId);
        if (days.isEmpty()) return new Streak(0, 0);

        // Use a set for O(1) day membership.
        Set<LocalDate> set = new HashSet<>(days);

        // Best streak: iterate sorted days.
        long best = 1;
        long cur = 1;
        for (int i = 1; i < days.size(); i++) {
            if (days.get(i).equals(days.get(i - 1).plusDays(1))) {
                cur++;
            } else {
                best = Math.max(best, cur);
                cur = 1;
            }
        }
        best = Math.max(best, cur);

        // Current streak: count backwards from today (or from last reading day if they didn't read today).
        LocalDate d = LocalDate.now();
        if (!set.contains(d)) {
            // allow streak to end at the last day they read
            d = days.get(days.size() - 1);
        }
        long current = 0;
        while (set.contains(d)) {
            current++;
            d = d.minusDays(1);
        }

        return new Streak(current, best);
    }

    @Override
    public YearOverview getYearOverview(long userId, int year) {
        LocalDate from = LocalDate.of(year, 1, 1);
        LocalDate to = LocalDate.of(year + 1, 1, 1);

        long pages = statisticsDao.getTotalPagesRead(userId, from, to);
        long activeDays = statisticsDao.getActiveDays(userId, from, to);
        double avgSession = statisticsDao.getAverageSessionMinutes(userId, from, to);

        // NOTE: we don't store "finishedAt" in DB; we show current total finished.
        long finished = countBooksByState(userId).getOrDefault(BookState.FINISHED, 0L);

        return new YearOverview(finished, pages, activeDays, avgSession);
    }

    @Override
    public Map<BookState, Long> countBooksByState(long userId) {
        return statisticsDao.countBooksByState(userId);
    }
}
