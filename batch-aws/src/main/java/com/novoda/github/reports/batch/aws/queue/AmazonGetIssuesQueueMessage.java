package com.novoda.github.reports.batch.aws.queue;

import com.google.auto.value.AutoValue;
import com.novoda.github.reports.batch.queue.GetIssuesQueueMessage;

import java.util.Date;

import org.jetbrains.annotations.Nullable;

@AutoValue
public abstract class AmazonGetIssuesQueueMessage implements AmazonQueueMessage, GetIssuesQueueMessage {

    public static AmazonGetIssuesQueueMessage create(Boolean terminal,
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

    @Override
    public String toShortString() {
        return String.format("%s/%s/ISSUES %s", organisationName(), repositoryName(), getPageAndTerminalString());
    }

}
