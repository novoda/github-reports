package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.aws.queue.AmazonGetCommentsQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetEventsQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetIssuesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetReviewCommentsQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.aws.NextMessagesTransformer;
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
                    true,
                    1L,
                    currentMessage.receiptHandle(),
                    currentMessage.organisationName(),
                    currentMessage.sinceOrNull(),
                    currentMessage.repositoryId(),
                    currentMessage.repositoryName(),
                    (long) item.getIssueNumber()
            ));
        }

        derived.add(AmazonGetCommentsQueueMessage.create(
                true,
                1L,
                currentMessage.receiptHandle(),
                currentMessage.organisationName(),
                currentMessage.sinceOrNull(),
                currentMessage.repositoryId(),
                currentMessage.repositoryName(),
                (long) item.getIssueNumber()
        ));

        derived.add(AmazonGetEventsQueueMessage.create(
                true,
                1L,
                currentMessage.receiptHandle(),
                currentMessage.organisationName(),
                currentMessage.sinceOrNull(),
                currentMessage.repositoryId(),
                currentMessage.repositoryName(),
                (long) item.getIssueNumber()
        ));

        return derived;
    }

}
