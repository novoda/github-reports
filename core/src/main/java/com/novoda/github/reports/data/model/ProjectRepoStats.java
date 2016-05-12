package com.novoda.github.reports.data.model;

public class ProjectRepoStats implements Stats {

    private final String projectRepoName;
    private final long numberOfOpenedIssues;
    private final long numberOfOpenedPullRequests;
    private final long numberOfCommentedIssues;
    private final long numberOfMergedPullRequests;
    private final long numberOfOtherEvents;
    private final long numberOfParticipatingUsers;

    public ProjectRepoStats(String projectRepoName,
                            long numberOfOpenedIssues,
                            long numberOfOpenedPullRequests,
                            long numberOfCommentedIssues,
                            long numberOfMergedPullRequests,
                            long numberOfOtherEvents,
                            long numberOfParticipatingUsers) {
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
                        "Number of opened issues: %d\n" +
                        "Number of opened PRs: %d\n" +
                        "Number of commented issues: %d\n" +
                        "Number of merged PRs: %d\n" +
                        "Number of other events: %d\n" +
                        "Number of participating users: %d",
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
