package com.novoda.github.reports.stats.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.Date;
import java.util.List;

@Parameters(commandDescription = "Retrieve data about a specific repository")
public class RepoOptions extends RangeOptions {

    @Parameter(description = "Repository name to retrieve data from")
    private List<String> repository;

    public RepoOptions(List<String> repository, Date from, Date to) {
        super(from, to);
        this.repository = repository;
    }

    public RepoOptions() {
        // no-op
    }

    public String getRepo() {
        if (repository != null && !repository.isEmpty()) {
            return repository.get(0);
        }
        return null;
    }
}
