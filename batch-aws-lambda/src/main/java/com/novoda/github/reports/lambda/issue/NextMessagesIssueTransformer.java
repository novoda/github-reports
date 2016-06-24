package com.novoda.github.reports.lambda.issue;

import com.novoda.github.reports.batch.aws.queue.AmazonGetCommentsQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonGetEventsQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonGetIssuesQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonGetReviewCommentsQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.lambda.NextMessagesTransformer;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.network.LastPageExtractor;
import com.novoda.github.reports.service.network.NextPageExtractor;

import java.util.ArrayList;
import java.util.List;

class NextMessagesIssueTransformer extends NextMessagesTransformer<RepositoryIssue, AmazonGetIssuesQueueMessage> {

    public static NextMessagesIssueTransformer newInstance(AmazonGetIssuesQueueMessage currentMessage) {
        NextPageExtractor nextPageExtractor = NextPageExtractor.newInstance();
        LastPageExtractor lastPageExtractor = LastPageExtractor.newInstance();
        return new NextMessagesIssueTransformer(currentMessage, nextPageExtractor, lastPageExtractor);
    }

    private NextMessagesIssueTransformer(AmazonGetIssuesQueueMessage currentMessage, NextPageExtractor nextPageExtractor, LastPageExtractor lastPageExtractor) {
        super(currentMessage, nextPageExtractor, lastPageExtractor);
    }

    @Override
    protected AmazonGetIssuesQueueMessage getNextPageMessage(boolean isTerminalMessage, int nextPage) {
        return AmazonGetIssuesQueueMessage.create(
                isTerminalMessage,
                (long) nextPage,
                currentMessage.receiptHandle(),
                currentMessage.organisationName(),
                currentMessage.sinceOrNull(),
                currentMessage.repositoryId(),
                currentMessage.repositoryName()
        );
    }

    @Override
    protected List<AmazonQueueMessage> getDerivedMessage(RepositoryIssue item) {
        List<AmazonQueueMessage> derived = new ArrayList<>();

        if (item.isPullRequest()) {
            derived.add(AmazonGetReviewCommentsQueueMessage.create(
                    ALWAYS_TERMINAL_MESSAGE,
                    FIRST_PAGE,
                    currentMessage.receiptHandle(),
                    currentMessage.organisationName(),
                    currentMessage.sinceOrNull(),
                    currentMessage.repositoryId(),
                    currentMessage.repositoryName(),
                    (long) item.getIssueNumber(),
                    item.getUserId(),
                    item.isPullRequest()
            ));
        }

        derived.add(AmazonGetCommentsQueueMessage.create(
                ALWAYS_TERMINAL_MESSAGE,
                FIRST_PAGE,
                currentMessage.receiptHandle(),
                currentMessage.organisationName(),
                currentMessage.sinceOrNull(),
                currentMessage.repositoryId(),
                currentMessage.repositoryName(),
                (long) item.getIssueNumber(),
                item.getUserId(),
                item.isPullRequest()
        ));

        derived.add(AmazonGetEventsQueueMessage.create(
                ALWAYS_TERMINAL_MESSAGE,
                FIRST_PAGE,
                currentMessage.receiptHandle(),
                currentMessage.organisationName(),
                currentMessage.sinceOrNull(),
                currentMessage.repositoryId(),
                currentMessage.repositoryName(),
                (long) item.getIssueNumber(),
                item.getUserId(),
                item.isPullRequest()
        ));

        return derived;
    }

}
