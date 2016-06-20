package com.novoda.github.reports.batch.queue;

public interface GetEventsQueueMessage extends GetIssuesQueueMessage {

    Long issueNumber();

}
