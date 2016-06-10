package com.novoda.github.reports.aws.queue;

import com.google.auto.value.AutoValue;

import java.util.Date;

@AutoValue
public abstract class AmazonGetEventsQueueMessage implements AmazonQueueMessage, GetCommentsQueueMessage {

    public static AmazonGetEventsQueueMessage create(boolean terminal,
                                                     Long page,
                                                     String receiptHandle,
                                                     String organisation,
                                                     Date since,
                                                     Long repositoryId,
                                                     String repositoryName,
                                                     Long issueNumber) {

        return new AutoValue_AmazonGetEventsQueueMessage(
                terminal,
                page,
                receiptHandle,
                organisation,
                since,
                repositoryId,
                repositoryName,
                issueNumber
        );
    }

}
