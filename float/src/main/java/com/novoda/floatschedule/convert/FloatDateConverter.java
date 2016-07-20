package com.novoda.floatschedule.convert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FloatDateConverter {

    private static final SimpleDateFormat FLOAT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Date NO_DATE = null;
    private static final String NO_STRING = null;

    public String toFloatDateFormat(Date date) {
        if (date == null) {
            return NO_STRING;
        }
        return FLOAT_DATE_FORMAT.format(date);
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
            return FLOAT_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            throw new InvalidFloatDateException(date, e);
        }
    }

}
