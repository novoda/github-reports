package com.novoda.github.reports.aws.queue;

import com.google.auto.value.AutoValue;

import java.util.Date;

@AutoValue
public abstract class AmazonGetRepositoriesQueueMessage implements AmazonQueueMessage, GetRepositoriesQueueMessage {

    public static AmazonGetRepositoriesQueueMessage create(boolean terminal,
                                                           Long page,
                                                           String receiptHandle,
                                                           String organisationName,
                                                           Date since) {

        return new AutoValue_AmazonGetRepositoriesQueueMessage(
                terminal,
                page,
                receiptHandle,
                organisationName,
                since
        );
    }

}
