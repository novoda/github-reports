package com.novoda.github.reports.batch.issue;

import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

class DateToISO8601Converter {

    String toISO8601OrNull(Date date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();
        return dateTimeFormatter.print(date.getTime());
    }

}
