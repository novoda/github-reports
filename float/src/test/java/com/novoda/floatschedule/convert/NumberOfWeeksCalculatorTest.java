package com.novoda.floatschedule.convert;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;

public class NumberOfWeeksCalculatorTest {

    private static final Date NO_DATE = null;

    private NumberOfWeeksCalculator numberOfWeeksCalculator;

    @Before
    public void setUp() {
        numberOfWeeksCalculator = new NumberOfWeeksCalculator();
    }

    @Test
    public void givenAStartDateOnAMondayAndEndDateOnNextSunday_whenCalculatingNumberOfWeeks_thenItIsOneWeek() {
        Date startDate = getDate(2016, Calendar.JULY, 4);
        Date endDate = getDate(2016, Calendar.JULY, 10);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksOrNullIn(startDate, endDate);

        assertEquals(1, actual);
    }

    @Test
    public void givenAStartDateByTheEndOfJanuaryAndEndDateInTheBeginningOfJuly_whenCalculatingNumberOfWeeks_thenItIsTheCorrectNumber() {
        Date startDate = getDate(2016, Calendar.JANUARY, 30);
        Date endDate = getDate(2016, Calendar.JULY, 5);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksOrNullIn(startDate, endDate);

        assertEquals(23, actual);
    }

    @Test
    public void givenAStartDateByTheEndOfTheYearAndEndDateInTheBeginningOfTheNext_whenCalculatingNumberOfWeeks_thenItIsTheCorrectNumber() {
        Date startDate = getDate(2016, Calendar.DECEMBER, 25);
        Date endDate = getDate(2017, Calendar.JANUARY, 30);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksOrNullIn(startDate, endDate);

        assertEquals(6, actual);
    }

    @Test
    public void givenStartAndEndDatesWithALeapDayInBetween_whenCalculatingNumberOfWeeks_thenItIsTheCorrectNumber() {
        Date startDate = getDate(2016, Calendar.FEBRUARY, 22);
        Date endDate = getDate(2016, Calendar.MARCH, 7);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksOrNullIn(startDate, endDate);

        assertEquals(3, actual);
    }

    @Test
    public void givenAStartDateOnAMondayAndEndDateOnNextMonday_whenCalculatingNumberOfWeeks_thenItIsTwoWeeks() {
        Date startDate = getDate(2016, Calendar.JULY, 4);
        Date endDate = getDate(2016, Calendar.JULY, 11);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksOrNullIn(startDate, endDate);

        assertEquals(2, actual);
    }

    @Test
    public void givenAStartDateMidWeekAndEndDateOnNextMonday_whenCalculatingNumberOfWeeks_thenItIsOneWeek() {
        Date startDate = getDate(2016, Calendar.JULY, 8);
        Date endDate = getDate(2016, Calendar.JULY, 11);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksOrNullIn(startDate, endDate);

        assertEquals(1, actual);
    }

    @Test
    public void givenAStartDateAndEndDateExactlyThreeWeeksLater_whenCalculatingNumberOfWeeks_thenItIsFourWeeks() {
        Date startDate = getDate(2016, Calendar.JULY, 5);
        Date endDate = getDate(2016, Calendar.JULY, 26);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksOrNullIn(startDate, endDate);

        assertEquals(4, actual);
    }

    @Test
    public void givenAStartDateAndEndDateExactlyAWeekSooner_whenCalculatingNumberOfWeeks_thenItIsNegativeOneWeek() {
        Date startDate = getDate(2016, Calendar.JULY, 5);
        Date endDate = getDate(2016, Calendar.JUNE, 28);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksOrNullIn(startDate, endDate);

        assertEquals(-1, actual);
    }

    @Test
    public void givenStartDateAndNoEndDate_whenCalculatingNumberOfWeeks_thenItIsNull() {
        Date startDate = getDate(2016, Calendar.JULY, 20);

        Integer actual = numberOfWeeksCalculator.getNumberOfWeeksOrNullIn(startDate, NO_DATE);

        assertEquals(null, actual);
    }

    @Test
    public void givenNoStartDateAndEndDate_whenCalculatingNumberOfWeeks_thenItIsNull() {
        Date endDate = getDate(2016, Calendar.AUGUST, 20);

        Integer actual = numberOfWeeksCalculator.getNumberOfWeeksOrNullIn(NO_DATE, endDate);

        assertEquals(null, actual);
    }

    @Test
    public void givenNoStartDateAndNoEndDate_whenCalculatingNumberOfWeeks_thenItIsNull() {

        Integer actual = numberOfWeeksCalculator.getNumberOfWeeksOrNullIn(NO_DATE, NO_DATE);

        assertEquals(null, actual);
    }

    private Date getDate(int year, int calendarMonth, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, calendarMonth, dayOfMonth);
        return calendar.getTime();
    }
}
