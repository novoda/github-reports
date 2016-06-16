package com.novoda.github.reports.batch.aws.repository;

import com.novoda.github.reports.aws.queue.AmazonGetIssuesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetRepositoriesQueueMessage;
import com.novoda.github.reports.batch.aws.NextMessagesTransformer;
import com.novoda.github.reports.service.network.LastPageExtractor;
import com.novoda.github.reports.service.network.NextPageExtractor;
import com.novoda.github.reports.service.repository.GithubRepository;

class NextMessagesRepositoryTransformer extends NextMessagesTransformer<GithubRepository, AmazonGetRepositoriesQueueMessage> {

    private static final long FIRST_PAGE = 1L;
    private static final boolean ALWAYS_TERMINAL_MESSAGE = true;

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
    protected AmazonGetIssuesQueueMessage getDerivedMessage(GithubRepository repository) {
        return AmazonGetIssuesQueueMessage.create(
                ALWAYS_TERMINAL_MESSAGE,
                FIRST_PAGE,
                currentMessage.receiptHandle(),
                currentMessage.organisationName(),
                currentMessage.sinceOrNull(),
                repository.getId(),
                repository.getName()
        );
    }
}
