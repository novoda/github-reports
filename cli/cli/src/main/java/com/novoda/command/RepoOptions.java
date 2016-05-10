package com.novoda.command;


import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;

@Parameters(commandDescription = "Retrieve data about a specific repository")
public class RepoOptions extends RangeOptions {

    @Parameter(description = "Repository name to retrieve data from")
    private List<String> repository;

    public String getRepo() {
        if (repository != null && !repository.isEmpty()) {
            return repository.get(0);
        }
        return null;
    }
}
