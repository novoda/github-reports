package com.novoda.github.reports.stats.command;

import com.beust.jcommander.IStringConverter;

import java.util.TimeZone;

public class TimeZoneConverter implements IStringConverter<TimeZone> {
    @Override
    public TimeZone convert(String value) {
        if (value == null) {
            return TimeZone.getDefault();
        }
        return TimeZone.getTimeZone(value);
    }
}
