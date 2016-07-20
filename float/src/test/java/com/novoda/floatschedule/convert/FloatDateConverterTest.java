package com.novoda.floatschedule.convert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FloatDateConverterTest {

    private FloatDateConverter dateConverter = new FloatDateConverter();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void givenADate_whenConvertingToFloatFormat_thenItConvertsAppropriately() throws Exception {
        Date date = givenADate(2016, Calendar.JULY, 1);

        String actual = dateConverter.toFloatDateFormat(date);

        assertEquals("2016-07-01", actual);
    }

    @Test
    public void givenNonExistingDateInFebruary_whenConvertingToFloatFormat_thenItShiftsDaysToMarchAndConvertsAppropriately() throws Exception {
        Date date = givenADate(1994, Calendar.FEBRUARY, 30);

        String actual = dateConverter.toFloatDateFormat(date);

        assertEquals("1994-03-02", actual);
    }

    @Test
    public void givenNullDate_whenConvertingToFloatFormat_thenItReturnsNullString() throws Exception {
        Date date = null;

        String actual = dateConverter.toFloatDateFormat(date);

        assertNull(actual);
    }

    @Test
    public void givenValidDateAsString_whenConvertingFromFloatFormat_thenItConvertsAppropriately() throws InvalidFloatDateException {
        String date = "2016-07-20";

        Date actual = dateConverter.fromFloatDateFormat(date);

        Date expected = givenADate(2016, Calendar.JULY, 20);

        assertEquals(0, expected.compareTo(actual));
    }

    @Test
    public void givenInvalidDateAsString_whenConvertingFromFloat_thenItThrowsInvalidFloatDateException() throws InvalidFloatDateException {
        String date = "clearly-invalid-date-lol";

        expectedException.expect(InvalidFloatDateException.class);
        dateConverter.fromFloatDateFormat(date);
    }

    @Test
    public void givenInvalidDateAsString_whenConvertingFromFloatOrNoDate_thenItReturnsNull() throws InvalidFloatDateException {
        String date = "clearly-invalid-date-lol";

        Date actual = dateConverter.fromFloatDateFormatOrNoDate(date);

        assertNull(actual);
    }

    private Date givenADate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

}
