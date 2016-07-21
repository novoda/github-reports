package com.novoda.github.reports.stats.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;

@Parameters(commandDescription = "Retrieve aggregated assigned/external statistics about users contributions")
public class AggregateOptions extends RangeOptions {

    @Parameter(description = "Users to retrieve data for (leave empty for all)")
    private List<String> users;

    public List<String> getUsers() {
        return users;
    }

}
