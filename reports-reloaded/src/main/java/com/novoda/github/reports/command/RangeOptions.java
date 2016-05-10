package com.novoda.github.reports.command;

import com.beust.jcommander.Parameter;

import java.util.Date;

abstract class RangeOptions implements Options {

    @Parameter(names = "--from", description = "Start of range", converter = ISO8601DateConverter.class)
    protected Date from;

    @Parameter(names = "--to", description = "End of range", converter = ISO8601DateConverter.class)
    protected Date to;

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }
}
