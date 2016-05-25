package com.novoda.github.reports.command;

import com.beust.jcommander.IStringConverter;

import java.util.Date;

import org.joda.time.DateTime;

class ISO8601DateConverter implements IStringConverter<Date> {
    @Override
    public Date convert(String value) {
        if (value == null) {
            return null;
        }
        return new DateTime(value).toDate();
    }
}
