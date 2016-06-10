package com.novoda.github.reports.aws.queue;

import com.google.auto.value.AutoValue;

import java.util.Date;

import org.jetbrains.annotations.Nullable;

@AutoValue
public abstract class AmazonGetRepositoriesQueueMessage implements AmazonQueueMessage, GetRepositoriesQueueMessage {

    public static AmazonGetRepositoriesQueueMessage create(boolean terminal,
                                                           Long page,
                                                           String receiptHandle,
                                                           String organisationName,
                                                           @Nullable Date since) {

        return new AutoValue_AmazonGetRepositoriesQueueMessage(
                terminal,
                page,
                receiptHandle,
                organisationName,
                since
        );
    }

}
