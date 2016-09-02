package com.novoda.github.reports.stats.command;

import com.beust.jcommander.Parameters;

import java.util.Date;
import java.util.List;

@Parameters(commandDescription = "Retrieve overall statistics about users contributions")
public class OverallOptions extends FloatTaskBasedOptions {

    // all parameters are in super class (from, to, users)

    public OverallOptions(List<String> users, Date from, Date to, String timezone) {
        super(users, from, to, timezone);
    }

    public OverallOptions() {
        // no-op
    }
}
