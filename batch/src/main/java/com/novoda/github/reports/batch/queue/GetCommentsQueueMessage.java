package com.novoda.github.reports.batch.queue;

public interface GetCommentsQueueMessage extends GetIssuesQueueMessage {

    Long issueNumber();

    Long issueOwnerId();

}
