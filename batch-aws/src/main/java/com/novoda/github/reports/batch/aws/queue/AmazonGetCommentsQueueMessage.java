package com.novoda.github.reports.batch.aws.queue;

import com.google.auto.value.AutoValue;
import com.novoda.github.reports.batch.queue.GetAllEventsQueueMessage;

import java.util.Date;

import org.jetbrains.annotations.Nullable;

@AutoValue
public abstract class AmazonGetCommentsQueueMessage implements AmazonQueueMessage, GetAllEventsQueueMessage {

    public static AmazonGetCommentsQueueMessage create(Boolean terminal,
                                                       Long page,
                                                       String receiptHandle,
                                                       String organisation,
                                                       @Nullable Date since,
                                                       Long repositoryId,
                                                       String repositoryName,
                                                       Long issueNumber,
                                                       Long issueOwnerId,
                                                       Boolean isPullRequest) {

        return new AutoValue_AmazonGetCommentsQueueMessage(
                terminal,
                page,
                receiptHandle,
                organisation,
                since,
                repositoryId,
                repositoryName,
                issueNumber,
                issueOwnerId,
                isPullRequest
        );
    }

    @Override
    public String toShortString() {
        return String.format("%s/%s/%d/COMMENTS %s", organisationName(), repositoryName(), issueNumber(), getPageAndTerminalString());
    }

}
