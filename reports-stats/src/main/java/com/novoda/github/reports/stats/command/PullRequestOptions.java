package com.novoda.github.reports.stats.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;

@Parameters(commandDescription = "Retrieve statistics about Pull Requests filtered on date, users, projects and repositories")
public class PullRequestOptions extends RangeOptions {

    @Parameter(names = {"--repositories", "--repos", "-r"},
            description = "Repositories to retrieve data from (can\'t be used together with \"--projects\")")
    private List<String> repositories;

    @Parameter(names = {"--projects", "-p"},
            description = "Projects to retrieve data from (can't be used together with \"--repositories\")")
    private List<String> projects;

    @Parameter(names = {"--users", "-u"},
            description = "Users to retrieve data for")
    private List<String> users;

    @Parameter(names = {"--group-by", "-g"},
            description = "Whether to add user average for every group",
            converter = PullRequestOptionsGroupByConverter.class)
    private PullRequestOptionsGroupBy groupBy;

    @Parameter(names = {"--average", "-a"},
            description = "Whether to add user average for every group")
    private boolean withAverage;

    public List<String> getRepositories() {
        return repositories;
    }

    public List<String> getProjects() {
        return projects;
    }

    public List<String> getUsers() {
        return users;
    }

    public PullRequestOptionsGroupBy getGroupBy() {
        return groupBy;
    }

    public boolean withAverage() {
        return withAverage;
    }

    public boolean hasRepositories() {
        return isListNotEmpty(repositories);
    }

    public boolean hasProjects() {
        return isListNotEmpty(projects);
    }

    private boolean isListNotEmpty(List<String> list) {
        return list != null && list.size() > 0;
    }
}
