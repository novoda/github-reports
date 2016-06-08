package com.novoda.github.reports.service.network;

import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class DateToISO8601Converter {

    public String toISO8601NoMillisOrNull(Date date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();
        return dateTimeFormatter.print(date.getTime());
    }

}
