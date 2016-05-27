package com.novoda.github.reports.stats.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;

@Parameters(commandDescription = "Retrieve data about a specific project")
public class ProjectOptions extends RangeOptions {

    @Parameter(description = "Project name to retrieve data from")
    private List<String> project;

    public String getProject() {
        if (project != null && !project.isEmpty()) {
            return project.get(0);
        }
        return null;
    }

}
