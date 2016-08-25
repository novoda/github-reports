package com.novoda.github.reports.batch.aws.queue;

import com.google.auto.value.AutoValue;
import com.novoda.github.reports.batch.queue.GetAllEventsQueueMessage;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@AutoValue
public abstract class AmazonGetReactionsQueueMessage implements AmazonQueueMessage, GetAllEventsQueueMessage {

    public static AmazonGetReactionsQueueMessage create(Boolean terminal,
                                                        Long page,
                                                        String receiptHandle,
                                                        String organisation,
                                                        @Nullable Date since,
                                                        Long repositoryId,
                                                        String repositoryName,
                                                        Long issueNumber,
                                                        Long issueOwnerId,
                                                        Boolean isPullRequest) {

        return new AutoValue_AmazonGetReactionsQueueMessage(
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
        return String.format("%s/%s/%d/REACTIONS %s", organisationName(), repositoryName(), issueNumber(), getPageAndTerminalString());
    }

}
