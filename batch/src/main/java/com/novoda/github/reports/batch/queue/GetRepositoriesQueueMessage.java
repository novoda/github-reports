package com.novoda.github.reports.batch.queue;

import java.util.Date;

import org.jetbrains.annotations.Nullable;

public interface GetRepositoriesQueueMessage extends QueueMessage {

    String organisationName();

    @Nullable Date sinceOrNull();

}
