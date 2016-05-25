package com.novoda.github.reports.data.model;

import java.math.BigInteger;

public class UserStats implements Stats {
    private final String userName;
    private final EventStats eventStats;
    private final BigInteger numberOfOtherPeopleComments;
    private final BigInteger numberOfRepositoriesWorkedOn;

    public UserStats(String userName,
                     EventStats eventStats,
                     BigInteger numberOfOtherPeopleComments,
                     BigInteger numberOfRepositoriesWorkedOn) {
        this.userName = userName;
        this.eventStats = eventStats;
        this.numberOfOtherPeopleComments = numberOfOtherPeopleComments;
        this.numberOfRepositoriesWorkedOn = numberOfRepositoriesWorkedOn;
    }

    public String getUserName() {
        return userName;
    }

    public EventStats getEventStats() {
        return eventStats;
    }

    public BigInteger getNumberOfOtherPeopleComments() {
        return numberOfOtherPeopleComments;
    }

    public BigInteger getNumberOfRepositoriesWorkedOn() {
        return numberOfRepositoriesWorkedOn;
    }

    public String describeStats() {
        return String.format(
                "Username: %s\n" +
                        "Number of opened issues: %s\n" +
                        "Number of opened PRs: %s\n" +
                        "Number of commented issues: %s\n" +
                        "Number of merged PRs: %s\n" +
                        "Number of other events: %s\n" +
                        "Number of other people's comments: %s\n" +
                        "Number of repositories worked on: %s",
                userName,
                eventStats.getNumberOfOpenedIssues(),
                eventStats.getNumberOfOpenedPullRequests(),
                eventStats.getNumberOfCommentedIssues(),
                eventStats.getNumberOfMergedPullRequests(),
                eventStats.getNumberOfOtherEvents(),
                numberOfOtherPeopleComments,
                numberOfRepositoriesWorkedOn
        );
    }
}
