package com.novoda.floatschedule.convert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class FloatDateConverter {

    private static final String FLOAT_DATE_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter FLOAT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FLOAT_DATE_FORMAT);
    private static final SimpleDateFormat FLOAT_SIMPLE_DATE_FORMATTER = new SimpleDateFormat(FLOAT_DATE_FORMAT);
    private static final Date NO_DATE = null;
    private static final String NO_STRING = null;

    public String toFloatDateFormat(Date date, String timezoneName) {
        if (date == null) {
            return NO_STRING;
        }
        TimeZone timezone = TimeZone.getTimeZone(timezoneName);
        ZoneId zoneId = timezone.toZoneId();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), zoneId);
        return localDateTime.format(FLOAT_DATE_TIME_FORMATTER);
    }

    public Date fromFloatDateFormatOrNoDate(String date) {
        try {
            return fromFloatDateFormat(date);
        } catch (InvalidFloatDateException e) {
            return NO_DATE;
        }
    }

    Date fromFloatDateFormat(String date) throws InvalidFloatDateException {
        try {
            return FLOAT_SIMPLE_DATE_FORMATTER.parse(date);
        } catch (ParseException e) {
            throw new InvalidFloatDateException(date, e);
        }
    }

}
