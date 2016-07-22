package com.novoda.github.reports.stats.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.Date;
import java.util.List;

@Parameters(commandDescription = "Retrieve statistics about Pull Requests filtered on date, users, projects and repositories")
public class PullRequestOptions extends RangeOptions {

    public PullRequestOptions(List<String> repositories,
                              List<String> organisationUsers,
                              PullRequestOptionsGroupBy groupBy,
                              boolean withAverage,
                              Date from,
                              Date to) {

        super(from, to);
        this.repositories = repositories;
        this.organisationUsers = organisationUsers;
        this.groupBy = groupBy;
        this.withAverage = withAverage;
    }

    @Parameter(names = {"--repositories", "--repos", "-r"},
            description = "Repositories to retrieve data from")
    private List<String> repositories;

    // TODO: remove after float integration
    @Parameter(names = {"--organisation", "--org", "-o"},
            description = "Users to treat as organisation (company) internals")
    private List<String> organisationUsers;

    @Parameter(names = {"--group-by", "-g"},
            description = "Whether to add user average for every group",
            converter = PullRequestOptionsGroupByConverter.class)
    private PullRequestOptionsGroupBy groupBy;

    @Parameter(names = {"--average", "-a"},
            description = "Whether to add user average for every group")
    private boolean withAverage;

    public PullRequestOptions() {
        // no-op
    }

    public List<String> getRepositories() {
        return repositories;
    }

    public List<String> getOrganisationUsers() {
        return organisationUsers;
    }

    public PullRequestOptionsGroupBy getGroupBy() {
        return groupBy;
    }

    public boolean withAverage() {
        return withAverage;
    }

}
