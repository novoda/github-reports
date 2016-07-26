package com.novoda.github.reports.stats.command;

import com.beust.jcommander.Parameter;

import java.util.Date;
import java.util.List;

public abstract class FloatTaskBasedOptions extends RangeOptions {

    @Parameter(description = "Users to retrieve data for (leave empty for all)")
    private List<String> users;

    public FloatTaskBasedOptions(List<String> users, Date from, Date to) {
        super(from, to);
        this.users = users;
    }

    public FloatTaskBasedOptions() {
        // no-op
    }

    public List<String> getUsers() {
        return users;
    }

}
