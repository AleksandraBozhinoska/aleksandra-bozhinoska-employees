package com.example.longestperiodpair.application.util;

import java.time.Duration;
import java.time.LocalDate;


/**
 * Utility class for custom date operations.
 */
public final class DateUtils {

    /**
     * Checks whether two time intervals overlap - (fStart,fEnd) and (sStart,
     * sEnd).
     *
     * @param fStart the start date of the first interval
     * @param fEnd   the end date of the first interval
     * @param sStart the start date of the second interval
     * @param sEnd   the end date of the second interval
     * @return boolean whether the intervals overlap
     */
    public static boolean checkIfIntervalsOverlap(LocalDate fStart,
                                                  LocalDate fEnd,
                                                  LocalDate sStart,
                                                  LocalDate sEnd) {
        return !(fEnd.isBefore(sStart) || sEnd.isBefore(fStart));
    }

    /**
     * Returns the overlap between the provided intervals in days.
     *
     * @param fStart the start date of the first interval
     * @param fEnd   the end date of the first interval
     * @param sStart the start date of the second interval
     * @param sEnd   the end date of the second interval
     * @return number of days in the intersection between the intervals
     */
    public static long findIntervalOverlapInDays(LocalDate fStart,
                                                 LocalDate fEnd,
                                                 LocalDate sStart,
                                                 LocalDate sEnd) {
        LocalDate latestStart = fStart;
        if (sStart.isAfter(latestStart)) {
            latestStart = sStart;
        }

        LocalDate earliestEnd = fEnd;
        if (sEnd.isBefore(earliestEnd)) {
            earliestEnd = sEnd;
        }

        /* assuming that the end date should be exclusive, otherwise we
        should add .plusDays(1) to the earliestEnd */
        return Duration
            .between(latestStart.atStartOfDay(), earliestEnd.atStartOfDay())
            .toDays();
    }
}
