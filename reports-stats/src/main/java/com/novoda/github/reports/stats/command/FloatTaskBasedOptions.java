package com.novoda.github.reports.stats.command;

import com.beust.jcommander.Parameter;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public abstract class FloatTaskBasedOptions extends RangeOptions {

    @Parameter(description = "Users to retrieve data for (leave empty for all)")
    private List<String> users;

    @Parameter(names = {"--timezone", "-tz"},
            description = "Timezone of the dates", converter = TimeZoneConverter.class)
    private TimeZone timezone = TimeZone.getDefault();

    public FloatTaskBasedOptions(List<String> users, Date from, Date to, TimeZone timezone) {
        super(from, to);
        this.users = users;
        this.timezone = timezone;
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
