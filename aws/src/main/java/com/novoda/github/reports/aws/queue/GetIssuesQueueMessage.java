package com.novoda.github.reports.aws.queue;

public interface GetIssuesQueueMessage extends GetRepositoriesQueueMessage {

    Long getRepositoryId();

}
