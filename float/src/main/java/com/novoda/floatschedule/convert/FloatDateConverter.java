package com.novoda.floatschedule.convert;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FloatDateConverter {

    private static final SimpleDateFormat FLOAT_DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd");

    public String toFloatDateFormat(Date date) {
        return FLOAT_DATE_FORMAT.format(date);
    }
}
