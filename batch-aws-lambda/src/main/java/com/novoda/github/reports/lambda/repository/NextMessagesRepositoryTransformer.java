package com.novoda.github.reports.lambda.repository;

import com.novoda.github.reports.batch.aws.queue.AmazonGetIssuesQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonGetRepositoriesQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.lambda.NextMessagesTransformer;
import com.novoda.github.reports.service.network.LastPageExtractor;
import com.novoda.github.reports.service.network.NextPageExtractor;
import com.novoda.github.reports.service.repository.GithubRepository;

import java.util.Collections;
import java.util.List;

class NextMessagesRepositoryTransformer extends NextMessagesTransformer<GithubRepository, AmazonGetRepositoriesQueueMessage> {

    public static NextMessagesRepositoryTransformer newInstance(AmazonGetRepositoriesQueueMessage message) {
        NextPageExtractor nextPageExtractor = NextPageExtractor.newInstance();
        LastPageExtractor lastPageExtractor = LastPageExtractor.newInstance();
        return new NextMessagesRepositoryTransformer(message, nextPageExtractor, lastPageExtractor);
    }

    private NextMessagesRepositoryTransformer(AmazonGetRepositoriesQueueMessage message,
                                              NextPageExtractor nextPageExtractor,
                                              LastPageExtractor lastPageExtractor) {

        super(message, nextPageExtractor, lastPageExtractor);
    }

    @Override
    protected AmazonGetRepositoriesQueueMessage getNextPageMessage(boolean isTerminalMessage, int nextPage) {
        return AmazonGetRepositoriesQueueMessage.create(
                isTerminalMessage,
                (long) nextPage,
                currentMessage.receiptHandle(),
                currentMessage.organisationName(),
                currentMessage.sinceOrNull()
        );
    }

    @Override
    protected List<AmazonQueueMessage> getDerivedMessage(GithubRepository repository) {
        return Collections.singletonList(AmazonGetIssuesQueueMessage.create(
                ALWAYS_TERMINAL_MESSAGE,
                FIRST_PAGE,
                currentMessage.receiptHandle(),
                currentMessage.organisationName(),
                currentMessage.sinceOrNull(),
                repository.getId(),
                repository.getName()
        ));
    }
}
