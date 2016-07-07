package com.novoda.floatschedule.convert;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FloatDateConverterTest {

    private FloatDateConverter dateConverter = new FloatDateConverter();

    @Test
    public void givenADate_whenConvertingToFloatFormat_thenItConvertsAppropriately() throws Exception {
        Date date = givenADate(2016, Calendar.JULY, 1);

        String actual = dateConverter.toFloatDateFormat(date);

        assertEquals("2016-07-01", actual);
    }

    @Test
    public void givenAnotherDate_whenConvertingToFloatFormat_thenItConvertsAppropriately() throws Exception {
        Date date = givenADate(1994, Calendar.FEBRUARY, 30);

        String actual = dateConverter.toFloatDateFormat(date);

        assertEquals("1994-03-02", actual);
    }

    private Date givenADate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

}
