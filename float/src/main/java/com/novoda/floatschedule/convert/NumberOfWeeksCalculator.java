package com.novoda.floatschedule.convert;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class NumberOfWeeksCalculator {

    private static final float NUMBER_OF_DAYS_IN_WEEK = 7f;

    /**
     * @return the number of weeks from start date to end date, rounded up. this means that, as an example, if there are 9 days in-between the
     * start and end dates, you'll get 2 weeks as a result.
     */
    public int getNumberOfWeeksIn(Date startDateInclusive, Date endDateInclusive) {
        LocalDate start = getLocalDateFrom(startDateInclusive);
        LocalDate end = getLocalDateFrom(endDateInclusive).plusDays(1);
        long numberOfDays = ChronoUnit.DAYS.between(start, end);
        return (int) getNumberOfWeeksInDays(numberOfDays);
    }

    private LocalDate getLocalDateFrom(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return LocalDate.of(calendar.get(Calendar.YEAR),
                            getMonthFrom(calendar),
                            calendar.get(Calendar.DAY_OF_MONTH));
    }

    private int getMonthFrom(Calendar calendar) {
        return calendar.get(Calendar.MONTH) + 1;
    }

    private double getNumberOfWeeksInDays(long numberOfDays) {
        float numberOfWeeks =  numberOfDays / NUMBER_OF_DAYS_IN_WEEK;
        return ceilIgnoringSign(numberOfWeeks);
    }

    private double ceilIgnoringSign(double value) {
        return value > 0 ? Math.ceil(value) : -Math.ceil(-value);
    }

}
