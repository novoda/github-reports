package com.novoda.github.reports.batch.queue;

public interface GetReviewCommentsQueueMessage extends GetIssuesQueueMessage {

    Long issueNumber();

}
