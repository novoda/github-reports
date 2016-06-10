package com.novoda.github.reports.aws.queue;

import com.google.auto.value.AutoValue;

import java.util.Date;

import org.jetbrains.annotations.Nullable;

@AutoValue
public abstract class AmazonGetReviewCommentsQueueMessage implements AmazonQueueMessage, GetCommentsQueueMessage {

    public static AmazonGetReviewCommentsQueueMessage create(boolean terminal,
                                                             Long page,
                                                             String receiptHandle,
                                                             String organisation,
                                                             @Nullable Date since,
                                                             Long repositoryId,
                                                             String repositoryName,
                                                             Long issueNumber) {

        return new AutoValue_AmazonGetReviewCommentsQueueMessage(
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
