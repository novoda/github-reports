package com.novoda.github.reports.data.model;

import java.math.BigDecimal;

public class UserStats implements Stats {
    private final String userName;
    private final BigDecimal numberOfOpenedIssues;
    private final BigDecimal numberOfOpenedPullRequests;
    private final BigDecimal numberOfOtherPeopleComments;
    private final BigDecimal numberOfMergedPullRequests;
    private final BigDecimal numberOfOtherEvents;
    private final BigDecimal numberOfProjectsWorkedOn;

    public UserStats(String userName,
                     BigDecimal numberOfOpenedIssues,
                     BigDecimal numberOfOpenedPullRequests,
                     BigDecimal numberOfOtherPeopleComments,
                     BigDecimal numberOfMergedPullRequests,
                     BigDecimal numberOfOtherEvents,
                     BigDecimal numberOfProjectsWorkedOn) {
        this.userName = userName;
        this.numberOfOpenedIssues = numberOfOpenedIssues;
        this.numberOfOpenedPullRequests = numberOfOpenedPullRequests;
        this.numberOfOtherPeopleComments = numberOfOtherPeopleComments;
        this.numberOfMergedPullRequests = numberOfMergedPullRequests;
        this.numberOfOtherEvents = numberOfOtherEvents;
        this.numberOfProjectsWorkedOn = numberOfProjectsWorkedOn;
    }

    public String describeStats() {
        return String.format(
                "Username: %s\n" +
                        "Number of opened issues: %s\n" +
                        "Number of opened PRs: %s\n" +
                        "Number of other people's comments: %s\n" +
                        "Number of merged PRs: %s\n" +
                        "Number of other events: %s\n" +
                        "Number of projects worked on: %s",
                userName,
                numberOfOpenedIssues,
                numberOfOpenedPullRequests,
                numberOfOtherPeopleComments,
                numberOfMergedPullRequests,
                numberOfOtherEvents,
                numberOfProjectsWorkedOn
        );
    }
}
