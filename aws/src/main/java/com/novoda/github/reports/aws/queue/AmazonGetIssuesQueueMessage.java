package com.novoda.github.reports.aws.queue;

import com.google.auto.value.AutoValue;

import java.util.Date;

import org.jetbrains.annotations.Nullable;

@AutoValue
public abstract class AmazonGetIssuesQueueMessage implements AmazonQueueMessage, GetIssuesQueueMessage {

    public static AmazonGetIssuesQueueMessage create(boolean terminal,
                                                     Long page,
                                                     String receiptHandle,
                                                     String organisation,
                                                     @Nullable Date since,
                                                     Long repositoryId,
                                                     String repositoryName) {

        return new AutoValue_AmazonGetIssuesQueueMessage(
                terminal,
                page,
                receiptHandle,
                organisation,
                since,
                repositoryId,
                repositoryName
        );
    }

}
