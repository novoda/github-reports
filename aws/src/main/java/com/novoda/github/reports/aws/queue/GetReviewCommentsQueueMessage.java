package com.novoda.github.reports.aws.queue;

public interface GetReviewCommentsQueueMessage extends GetIssuesQueueMessage {

    Long getIssueNumber();

}
