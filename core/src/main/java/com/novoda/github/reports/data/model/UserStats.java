package com.novoda.github.reports.data.model;

public class UserStats implements Stats {
    private final String userName;
    private final long numberOfOpenedIssues;
    private final long numberOfOpenedPullRequests;
    private final long numberOfOtherPeopleComments;
    private final long numberOfMergedPullRequests;
    private final long numberOfOtherEvents;
    private final long numberOfProjectsWorkedOn;

    public UserStats(String userName,
                     long numberOfOpenedIssues,
                     long numberOfOpenedPullRequests,
                     long numberOfOtherPeopleComments,
                     long numberOfMergedPullRequests,
                     long numberOfOtherEvents,
                     long numberOfProjectsWorkedOn) {
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
                        "Number of opened issues: %d\n" +
                        "Number of opened PRs: %d\n" +
                        "Number of other people's comments: %d\n" +
                        "Number of merged PRs: %d\n" +
                        "Number of other events: %d\n" +
                        "Number of projects worked on: %d",
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
