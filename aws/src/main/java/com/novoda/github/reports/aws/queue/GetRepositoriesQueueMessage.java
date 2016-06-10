package com.novoda.github.reports.aws.queue;

import java.util.Date;

import org.jetbrains.annotations.Nullable;

public interface GetRepositoriesQueueMessage extends QueueMessage {

    String organisationName();

    @Nullable Date since();

}
