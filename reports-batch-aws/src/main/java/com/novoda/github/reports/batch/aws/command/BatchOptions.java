package com.novoda.github.reports.batch.aws.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.ISO8601DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BatchOptions {

    @Parameter(description = "Organisation to retrieve data for")
    private List<String> organisation;

    @Parameter(names = "--from", description = "Start of range", converter = ISO8601DateConverter.class)
    private Date from;

    @Parameter(names = "--email", description = "Email address to report job completion to", variableArity = true)
    private List<String> emails = new ArrayList<>();

    public String getOrganisation() {
        if (organisation != null && !organisation.isEmpty()) {
            return organisation.get(0);
        }
        throw new IllegalArgumentException("You need to specify the organisation at the very least.");
    }

    public Date getFrom() {
        return from;
    }

    public List<String> getEmails() {
        return emails;
    }
}
