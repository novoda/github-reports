package com.novoda.github.reports.batch.issue;

import java.util.Date;

import org.joda.time.DateTime;

class DateToISO8601Converter {

    String toISO8601OrNull(Date date) {
        if (date == null) {
            return null;
        }
        return new DateTime(date.getTime()).toString();
    }

}
