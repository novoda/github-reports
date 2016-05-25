package com.novoda.github.reports.data.model;

import java.math.BigInteger;

public class EventStats {
    private final BigInteger numberOfOpenedIssues;
    private final BigInteger numberOfOpenedPullRequests;
    private final BigInteger numberOfCommentedIssues;
    private final BigInteger numberOfMergedPullRequests;
    private final BigInteger numberOfOtherEvents;

    public EventStats(BigInteger numberOfOpenedIssues,
                      BigInteger numberOfOpenedPullRequests,
                      BigInteger numberOfCommentedIssues,
                      BigInteger numberOfMergedPullRequests,
                      BigInteger numberOfOtherEvents) {
        this.numberOfOpenedIssues = numberOfOpenedIssues;
        this.numberOfOpenedPullRequests = numberOfOpenedPullRequests;
        this.numberOfCommentedIssues = numberOfCommentedIssues;
        this.numberOfMergedPullRequests = numberOfMergedPullRequests;
        this.numberOfOtherEvents = numberOfOtherEvents;
    }

    public BigInteger getNumberOfOpenedIssues() {
        return numberOfOpenedIssues;
    }

    public BigInteger getNumberOfOpenedPullRequests() {
        return numberOfOpenedPullRequests;
    }

    public BigInteger getNumberOfCommentedIssues() {
        return numberOfCommentedIssues;
    }

    public BigInteger getNumberOfMergedPullRequests() {
        return numberOfMergedPullRequests;
    }

    public BigInteger getNumberOfOtherEvents() {
        return numberOfOtherEvents;
    }
}
