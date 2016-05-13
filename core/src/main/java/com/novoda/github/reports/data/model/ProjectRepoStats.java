package com.novoda.github.reports.data.model;

import java.math.BigDecimal;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class ProjectRepoStats implements Stats {

    private final String projectRepoName;
    private final BigDecimal numberOfOpenedIssues;
    private final BigDecimal numberOfOpenedPullRequests;
    private final BigDecimal numberOfCommentedIssues;
    private final BigDecimal numberOfMergedPullRequests;
    private final BigDecimal numberOfOtherEvents;
    private final BigDecimal numberOfParticipatingUsers;

    public ProjectRepoStats(String projectRepoName,
                            BigDecimal numberOfOpenedIssues,
                            BigDecimal numberOfOpenedPullRequests,
                            BigDecimal numberOfCommentedIssues,
                            BigDecimal numberOfMergedPullRequests,
                            BigDecimal numberOfOtherEvents,
                            BigDecimal numberOfParticipatingUsers) {
        this.projectRepoName = projectRepoName;
        this.numberOfOpenedIssues = numberOfOpenedIssues;
        this.numberOfOpenedPullRequests = numberOfOpenedPullRequests;
        this.numberOfCommentedIssues = numberOfCommentedIssues;
        this.numberOfMergedPullRequests = numberOfMergedPullRequests;
        this.numberOfOtherEvents = numberOfOtherEvents;
        this.numberOfParticipatingUsers = numberOfParticipatingUsers;
    }

    public String describeStats() {
        return String.format(
                "Name: %s\n" +
                        "Number of opened issues: %s\n" +
                        "Number of opened PRs: %s\n" +
                        "Number of commented issues: %s\n" +
                        "Number of merged PRs: %s\n" +
                        "Number of other events: %s\n" +
                        "Number of participating users: %s",
                projectRepoName,
                numberOfOpenedIssues,
                numberOfOpenedPullRequests,
                numberOfCommentedIssues,
                numberOfMergedPullRequests,
                numberOfOtherEvents,
                numberOfParticipatingUsers
        );
    }
}
