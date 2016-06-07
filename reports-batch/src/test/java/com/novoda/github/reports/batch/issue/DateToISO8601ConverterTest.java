package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.network.DateToISO8601Converter;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateToISO8601ConverterTest {

    private DateToISO8601Converter dateToISO8601Converter;

    @Before
    public void setUp() throws Exception {
        dateToISO8601Converter = new DateToISO8601Converter();
    }

    @Test
    public void givenADate_whenConverting_thenTheDateIsConvertedToAnISO8601String() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.UK);
        calendar.set(2016, Calendar.MAY, 25, 12, 0, 30);

        String actual = dateToISO8601Converter.toISO8601NoMillisOrNull(calendar.getTime());

        assertEquals("2016-05-25T13:00:30+01:00", actual);
    }

    @Test
    public void givenANullDate_whenConverting_thenTheDateIsConvertedToAnISO8601String() {
        Date date = null;

        String actual = dateToISO8601Converter.toISO8601NoMillisOrNull(date);

        assertEquals(null, actual);
    }

}
