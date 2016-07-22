package com.novoda.github.reports.stats.command;

import com.beust.jcommander.Parameter;

import java.util.Date;

abstract class RangeOptions implements Options {

    @Parameter(names = "--from", description = "Start of range", converter = ISO8601DateConverter.class)
    private Date from;

    @Parameter(names = "--to", description = "End of range", converter = ISO8601DateConverter.class)
    private Date to;

    public RangeOptions() {
    }

    public RangeOptions(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }
}
