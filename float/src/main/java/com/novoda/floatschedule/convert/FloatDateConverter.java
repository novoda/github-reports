package com.novoda.floatschedule.convert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FloatDateConverter {

    private static final SimpleDateFormat FLOAT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Date NO_DATE = null;

    public String toFloatDateFormat(Date date) {
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
