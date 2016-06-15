package com.novoda.github.reports.batch.aws.repository;

import com.novoda.github.reports.aws.queue.AmazonGetIssuesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetRepositoriesQueueMessage;
import com.novoda.github.reports.batch.aws.NextMessagesTransformer;
import com.novoda.github.reports.service.network.LastPageExtractor;
import com.novoda.github.reports.service.network.NextPageExtractor;
import com.novoda.github.reports.service.repository.GithubRepository;

import java.util.List;

import retrofit2.Response;

public class NextMessagesRepositoryTransformer extends NextMessagesTransformer<GithubRepository, AmazonGetRepositoriesQueueMessage> {

    protected NextMessagesRepositoryTransformer(NextPageExtractor nextPageExtractor,
                                                LastPageExtractor lastPageExtractor,
                                                Response<List<GithubRepository>> response,
                                                AmazonGetRepositoriesQueueMessage message) {

        super(nextPageExtractor, lastPageExtractor, response, message);
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
    protected AmazonGetIssuesQueueMessage getOtherMessage(GithubRepository repository) {
        return AmazonGetIssuesQueueMessage.create(
                true,
                1L,
                currentMessage.receiptHandle(),
                currentMessage.organisationName(),
                currentMessage.sinceOrNull(),
                repository.getId(),
                repository.getName()
        );
    }
}
