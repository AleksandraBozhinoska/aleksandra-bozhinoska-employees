package com.example.longestperiodpair.application.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link DateUtils}.
 */
class DateUtilsTest {

    @Test
    void shouldFindOverlap() {
        var firstStart = LocalDate.of(2020, 4, 15);
        var firstEnd = LocalDate.of(2020, 4, 20);
        var secondStart = LocalDate.of(2020, 4, 15);
        var secondEnd = LocalDate.of(2020, 4, 18);

        assertTrue(DateUtils
            .checkIfIntervalsOverlap(firstStart, firstEnd, secondStart,
                secondEnd));
    }

    @Test
    void shouldNotFindOverlap() {
        var firstStart = LocalDate.of(2020, 4, 15);
        var firstEnd = LocalDate.of(2020, 4, 20);
        var secondStart = LocalDate.of(2020, 4, 20);
        var secondEnd = LocalDate.of(2020, 4, 23);

        assertFalse(DateUtils
            .checkIfIntervalsOverlap(firstStart, firstEnd, secondStart,
                secondEnd));
    }

    @Test
    void shouldCountOverlapInDays() {
        var firstStart = LocalDate.of(2020, 4, 16);
        var firstEnd = LocalDate.of(2020, 4, 18);
        var secondStart = LocalDate.of(2020, 4, 15);
        var secondEnd = LocalDate.of(2020, 4, 18);

        assertEquals(2, DateUtils
            .findIntervalOverlapInDays(firstStart, firstEnd, secondStart,
                secondEnd));
    }
}
