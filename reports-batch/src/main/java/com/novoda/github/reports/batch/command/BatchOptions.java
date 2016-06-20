package com.novoda.github.reports.batch.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.ISO8601DateConverter;

import java.util.Date;
import java.util.List;

public abstract class BatchOptions {

    @Parameter(description = "Organisation to retrieve data for")
    private List<String> organisation;

    @Parameter(names = "--from", description = "Start of range", converter = ISO8601DateConverter.class)
    private Date from;

    public String getOrganisation() {
        if (organisation != null && !organisation.isEmpty()) {
            return organisation.get(0);
        }
        throw new IllegalArgumentException("You need to specify the organisation at the very least.");
    }

    public Date getFrom() {
        return from;
    }

}
