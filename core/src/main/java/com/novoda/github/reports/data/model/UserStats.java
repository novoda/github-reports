package com.novoda.github.reports.data.model;

import java.math.BigInteger;

public class UserStats implements Stats {
    private final String userName;
    private final BigInteger numberOfOpenedIssues;
    private final BigInteger numberOfOpenedPullRequests;
    private final BigInteger numberOfCommentedIssues;
    private final BigInteger numberOfOtherPeopleComments;
    private final BigInteger numberOfMergedPullRequests;
    private final BigInteger numberOfOtherEvents;
    private final BigInteger numberOfRepositoriesWorkedOn;

    public UserStats(String userName,
                     BigInteger numberOfOpenedIssues,
                     BigInteger numberOfOpenedPullRequests,
                     BigInteger numberOfCommentedIssues,
                     BigInteger numberOfOtherPeopleComments,
                     BigInteger numberOfMergedPullRequests,
                     BigInteger numberOfOtherEvents,
                     BigInteger numberOfRepositoriesWorkedOn) {
        this.userName = userName;
        this.numberOfOpenedIssues = numberOfOpenedIssues;
        this.numberOfOpenedPullRequests = numberOfOpenedPullRequests;
        this.numberOfCommentedIssues = numberOfCommentedIssues;
        this.numberOfOtherPeopleComments = numberOfOtherPeopleComments;
        this.numberOfMergedPullRequests = numberOfMergedPullRequests;
        this.numberOfOtherEvents = numberOfOtherEvents;
        this.numberOfRepositoriesWorkedOn = numberOfRepositoriesWorkedOn;
    }

    public String describeStats() {
        return String.format(
                "Username: %s\n" +
                        "Number of opened issues: %s\n" +
                        "Number of opened PRs: %s\n" +
                        "Number of commented issues: %s\n" +
                        "Number of other people's comments: %s\n" +
                        "Number of merged PRs: %s\n" +
                        "Number of other events: %s\n" +
                        "Number of repositories worked on: %s",
                userName,
                numberOfOpenedIssues,
                numberOfOpenedPullRequests,
                numberOfCommentedIssues,
                numberOfOtherPeopleComments,
                numberOfMergedPullRequests,
                numberOfOtherEvents,
                numberOfRepositoriesWorkedOn
        );
    }
}
