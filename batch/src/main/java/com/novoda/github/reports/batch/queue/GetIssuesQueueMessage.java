package com.novoda.github.reports.batch.queue;

public interface GetIssuesQueueMessage extends GetRepositoriesQueueMessage {

    Long repositoryId();

    String repositoryName();

}
