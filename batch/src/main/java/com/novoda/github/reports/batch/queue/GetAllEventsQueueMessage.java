package com.novoda.github.reports.batch.queue;

public interface GetAllEventsQueueMessage extends GetIssuesQueueMessage {

    Long issueNumber();

    Long issueOwnerId();

    Boolean isPullRequest();

}
