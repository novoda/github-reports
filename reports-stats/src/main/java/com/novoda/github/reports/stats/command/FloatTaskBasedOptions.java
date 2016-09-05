package com.novoda.github.reports.stats.command;

import com.beust.jcommander.Parameter;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public abstract class FloatTaskBasedOptions extends RangeOptions {

    @Parameter(description = "Users to retrieve data for (leave empty for all)")
    private List<String> users;

    @Parameter(description = "Timezone of the dates", converter = TimeZoneConverter.class)
    private TimeZone timezone;

    public FloatTaskBasedOptions(List<String> users, Date from, Date to, TimeZone timezone) {
        super(from, to);
        this.users = users;
    }

    public FloatTaskBasedOptions() {
        // no-op
    }

    public List<String> getUsers() {
        return users;
    }

    public TimeZone getTimezone() {
        return timezone;
    }
}
