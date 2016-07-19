package com.novoda.floatschedule.convert;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class NumberOfWeeksCalculatorTest {

    private NumberOfWeeksCalculator numberOfWeeksCalculator;

    @Before
    public void setUp() {
        numberOfWeeksCalculator = new NumberOfWeeksCalculator();
    }

    @Test
    public void givenAStartDateOnAMondayAndEndDateOnNextSunday_whenCalculatingNumberOfWeeks_thenItIsOneWeek() {
        Date startDate = getDate(2016, Calendar.JULY, 4);
        Date endDate = getDate(2016, Calendar.JULY, 10);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksIn(startDate, endDate);

        assertEquals(1, actual);
    }

    @Test
    public void givenAStartDateByTheEndOfJanuaryAndEndDateInTheBeginningOfJuly_whenCalculatingNumberOfWeeks_thenItIsTheCorrectNumber() {
        Date startDate = getDate(2016, Calendar.JANUARY, 30);
        Date endDate = getDate(2016, Calendar.JULY, 5);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksIn(startDate, endDate);

        assertEquals(23, actual);
    }

    @Test
    public void givenAStartDateByTheEndOfTheYearAndEndDateInTheBeginningOfTheNext_whenCalculatingNumberOfWeeks_thenItIsTheCorrectNumber() {
        Date startDate = getDate(2016, Calendar.DECEMBER, 25);
        Date endDate = getDate(2017, Calendar.JANUARY, 30);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksIn(startDate, endDate);

        assertEquals(6, actual);
    }

    @Test
    public void givenAStartDateOnAMondayAndEndDateOnNextMonday_whenCalculatingNumberOfWeeks_thenItIsTwoWeeks() {
        Date startDate = getDate(2016, Calendar.JULY, 4);
        Date endDate = getDate(2016, Calendar.JULY, 11);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksIn(startDate, endDate);

        assertEquals(2, actual);
    }

    @Test
    public void givenAStartDateMidWeekAndEndDateOnNextMonday_whenCalculatingNumberOfWeeks_thenItIsOneWeek() {
        Date startDate = getDate(2016, Calendar.JULY, 8);
        Date endDate = getDate(2016, Calendar.JULY, 11);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksIn(startDate, endDate);

        assertEquals(1, actual);
    }

    @Test
    public void givenAStartDateAndEndDateExactlyThreeWeeksLater_whenCalculatingNumberOfWeeks_thenItIsFourWeeks() {
        Date startDate = getDate(2016, Calendar.JULY, 5);
        Date endDate = getDate(2016, Calendar.JULY, 26);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksIn(startDate, endDate);

        assertEquals(4, actual);
    }

    @Test
    public void givenAStartDateAndEndDateExactlyAWeekSooner_whenCalculatingNumberOfWeeks_thenItIsNegativeOneWeek() {
        Date startDate = getDate(2016, Calendar.JULY, 5);
        Date endDate = getDate(2016, Calendar.JUNE, 28);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksIn(startDate, endDate);

        assertEquals(-1, actual);
    }

    private Date getDate(int year, int calendarMonth, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, calendarMonth, dayOfMonth);
        return calendar.getTime();
    }
}
