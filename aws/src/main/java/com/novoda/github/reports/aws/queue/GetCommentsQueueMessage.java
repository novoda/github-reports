package com.novoda.github.reports.aws.queue;

public interface GetCommentsQueueMessage extends GetIssuesQueueMessage {

    Long issueNumber();

}
