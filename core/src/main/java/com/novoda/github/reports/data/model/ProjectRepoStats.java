package com.novoda.github.reports.data.model;

import java.math.BigInteger;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class ProjectRepoStats implements Stats {

    private final String projectRepoName;
    private final BigInteger numberOfOpenedIssues;
    private final BigInteger numberOfOpenedPullRequests;
    private final BigInteger numberOfCommentedIssues;
    private final BigInteger numberOfMergedPullRequests;
    private final BigInteger numberOfOtherEvents;
    private final BigInteger numberOfParticipatingUsers;

    public ProjectRepoStats(String projectRepoName,
                            BigInteger numberOfOpenedIssues,
                            BigInteger numberOfOpenedPullRequests,
                            BigInteger numberOfCommentedIssues,
                            BigInteger numberOfMergedPullRequests,
                            BigInteger numberOfOtherEvents,
                            BigInteger numberOfParticipatingUsers) {
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
