package com.novoda.github.reports.batch.aws.queue;

import com.google.auto.value.AutoValue;
import com.novoda.github.reports.batch.queue.GetCommentsQueueMessage;

import java.util.Date;

import org.jetbrains.annotations.Nullable;

@AutoValue
public abstract class AmazonGetCommentsQueueMessage implements AmazonQueueMessage, GetCommentsQueueMessage {

    public static AmazonGetCommentsQueueMessage create(Boolean terminal,
                                                       Long page,
                                                       String receiptHandle,
                                                       String organisation,
                                                       @Nullable Date since,
                                                       Long repositoryId,
                                                       String repositoryName,
                                                       Long issueNumber,
                                                       Long issueOwnerId) {

        return new AutoValue_AmazonGetCommentsQueueMessage(
                terminal,
                page,
                receiptHandle,
                organisation,
                since,
                repositoryId,
                repositoryName,
                issueNumber,
                issueOwnerId
        );
    }

    @Override
    public String toShortString() {
        return String.format("%s/%s/%d/COMMENTS %s", organisationName(), repositoryName(), issueNumber(), getPageAndTerminalString());
    }

}