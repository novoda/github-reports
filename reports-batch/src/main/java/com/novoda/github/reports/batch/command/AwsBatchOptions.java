package com.novoda.github.reports.batch.command;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class AwsBatchOptions extends BatchOptions {

    @Parameter(names = "--job", description = "Name of the job")
    private String job;

    @Parameter(names = "--email", description = "Email address to report job termination to", variableArity = true)
    private List<String> emails = new ArrayList<>();

    public String getJob() {
        return job;
    }

    public List<String> getEmails() {
        return emails;
    }

}
