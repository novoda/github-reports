package com.novoda.github.reports.data.model;

import java.math.BigInteger;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class ProjectRepoStats implements Stats {

    private final String projectRepoName;
    private final EventStats eventStats;
    private final BigInteger numberOfParticipatingUsers;

    public ProjectRepoStats(String projectRepoName,
                            EventStats eventStats,
                            BigInteger numberOfParticipatingUsers) {
        this.projectRepoName = projectRepoName;
        this.eventStats = eventStats;
        this.numberOfParticipatingUsers = numberOfParticipatingUsers;
    }

    public String getProjectRepoName() {
        return projectRepoName;
    }

    public EventStats getEventStats() {
        return eventStats;
    }

    public BigInteger getNumberOfParticipatingUsers() {
        return numberOfParticipatingUsers;
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
                eventStats.getNumberOfOpenedIssues(),
                eventStats.getNumberOfOpenedPullRequests(),
                eventStats.getNumberOfCommentedIssues(),
                eventStats.getNumberOfMergedPullRequests(),
                eventStats.getNumberOfOtherEvents(),
                numberOfParticipatingUsers
        );
    }
}
