package com.novoda.github.reports.stats.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.Date;
import java.util.List;

@Parameters(commandDescription = "Retrieve data about a specific user")
public class UserOptions extends RangeOptions {

    @Parameter(description = "Github username")
    private List<String> user;

    @Parameter(names = "--repo", description = "Name of the repository to retrieve data from")
    private String repository;

    public UserOptions(List<String> user, String repository, Date from, Date to) {
        super(from, to);
        this.user = user;
        this.repository = repository;
    }

    public UserOptions() {
        // no-op
    }

    public String getUser() {
        if (user != null && !user.isEmpty()) {
            return user.get(0);
        }
        return null;
    }

    public String getRepository() {
        return repository;
    }

}
