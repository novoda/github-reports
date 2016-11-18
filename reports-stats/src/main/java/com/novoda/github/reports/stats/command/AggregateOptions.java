package com.novoda.github.reports.stats.command;

import com.beust.jcommander.Parameters;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Parameters(commandDescription = "Retrieve aggregated assigned/external statistics about users contributions")
public class AggregateOptions extends FloatTaskBasedOptions {

    // all parameters are in super class (from, to, users)

    public AggregateOptions(List<String> users, Date from, Date to, TimeZone timezone) {
        super(users, from, to, timezone);
    }

    public AggregateOptions() {
        // no-op
    }
}
