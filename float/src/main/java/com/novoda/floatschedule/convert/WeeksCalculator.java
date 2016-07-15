package com.novoda.floatschedule.convert;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

public class WeeksCalculator {

    private static final int NUMBER_OF_DAYS_IN_WEEK = 7;

    public int getNumberOfWeeksIn(Date startDate, Date endDate) {
        LocalDate start = getLocalDateFrom(startDate);
        LocalDate end = getLocalDateFrom(endDate);
        Period period = Period.between(start, end);
        return period.getDays() / NUMBER_OF_DAYS_IN_WEEK;
    }

    private LocalDate getLocalDateFrom(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return LocalDate.of(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
    }

}
