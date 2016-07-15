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
    public void givenAStartDateOnAMondayAndEndDateOnNextMonday_whenCalculatingNumberOfWeeks_theItIsOneWeek() {
        Date startDate = getDate(2016, Calendar.JULY, 4);
        Date endDate = getDate(2016, Calendar.JULY, 11);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksIn(startDate, endDate);

        assertEquals(1, actual);
    }

    @Test
    public void givenAStartDateMidWeekAndEndDateOnNextMonday_whenCalculatingNumberOfWeeks_theItIsZeroWeeks() {
        Date startDate = getDate(2016, Calendar.JULY, 8);
        Date endDate = getDate(2016, Calendar.JULY, 11);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksIn(startDate, endDate);

        assertEquals(0, actual);
    }

    @Test
    public void givenAStartDateAndEndDateExactlyThreeWeeksLater_whenCalculatingNumberOfWeeks_theItIsThreeWeeks() {
        Date startDate = getDate(2016, Calendar.JULY, 5);
        Date endDate = getDate(2016, Calendar.JULY, 26);

        int actual = numberOfWeeksCalculator.getNumberOfWeeksIn(startDate, endDate);

        assertEquals(3, actual);
    }

    @Test
    public void givenAStartDateAndEndDateExacltyAWeekSooner_whenCalculatingNumberOfWeeks_theItIsNegativeOneWeeks() {
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
