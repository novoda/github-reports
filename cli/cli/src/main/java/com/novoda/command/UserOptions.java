package com.novoda.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;

@Parameters(commandDescription = "Retrieve data about a specific user")
public class UserOptions extends RangeOptions {

    @Parameter(description = "Github username")
    private List<String> user;

    @Parameter(names = "--repo", description = "Name of the repository to retrieve data from")
    private String repository;

    @Parameter(names = "--project", description = "Project name to retrieve data from")
    private String project;

    public String getUser() {
        if (user != null && !user.isEmpty()) {
            return user.get(0);
        }
        return null;
    }

    public String getRepository() {
        return repository;
    }

    public String getProject() {
        return project;
    }
}
