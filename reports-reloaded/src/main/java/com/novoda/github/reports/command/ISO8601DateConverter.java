package com.novoda.github.reports.command;

import com.beust.jcommander.IStringConverter;
import org.joda.time.DateTime;

import java.util.Date;

public class ISO8601DateConverter implements IStringConverter<Date> {
    @Override
    public Date convert(String value) {
        if (value == null) {
            return null;
        }
        return new DateTime(value).toDate();
    }
}
