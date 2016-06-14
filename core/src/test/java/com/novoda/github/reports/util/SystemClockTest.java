package com.novoda.github.reports.util;

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SystemClockTest {

    private SystemClock systemClock;

    @Before
    public void setUp() {
        systemClock = SystemClock.newInstance();
    }

    @Test
    public void givenDatesWithin2Minutes_whenGetDifferenceInMinutes_thenReturn2() {
        Date from = givenDate(2016, 5, 14, 16, 33, 0);
        Date to = givenDate(2016, 5, 14, 16, 35, 0);

        long actual = systemClock.getDifferenceInMinutes(from, to);

        assertEquals(2, actual);
    }

    @Test
    public void givenDatesWithin2MinutesAnd10Seconds_whenGetDifferenceInMinutes_thenReturn3() {
        Date from = givenDate(2016, 5, 14, 16, 33, 0);
        Date to = givenDate(2016, 5, 14, 16, 35, 10);

        long actual = systemClock.getDifferenceInMinutes(from, to);

        assertEquals(3, actual);
    }

    @Test
    public void givenDatesWithin10Seconds_whenGetDifferenceInMinutes_thenReturn1() {
        Date from = givenDate(2016, 5, 14, 16, 33, 0);
        Date to = givenDate(2016, 5, 14, 16, 33, 10);

        long actual = systemClock.getDifferenceInMinutes(from, to);

        assertEquals(1, actual);
    }

    @Test
    public void givenEqualDates_whenGetDifferenceInMinutes_thenReturn0() {
        Date from = givenDate(2016, 5, 14, 16, 33, 0);
        Date to = givenDate(2016, 5, 14, 16, 33, 0);

        long actual = systemClock.getDifferenceInMinutes(from, to);

        assertEquals(0, actual);
    }

    @Test
    public void givenInvertedDates_whenGetDifferenceInMinutes_thenReturn0() {
        Date from = givenDate(2016, 5, 14, 16, 35, 0);
        Date to = givenDate(2016, 5, 14, 16, 33, 0);

        long actual = systemClock.getDifferenceInMinutes(from, to);

        assertEquals(0, actual);
    }

    private Date givenDate(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second).getTime();
    }

}
