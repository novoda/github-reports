package com.novoda.github.reports.batch.command;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class AwsBatchOptions extends BatchOptions {

    @Parameter(names = "--email", description = "Email address to report job completion to", variableArity = true)
    private List<String> emails = new ArrayList<>();

    public List<String> getEmails() {
        return emails;
    }

}
