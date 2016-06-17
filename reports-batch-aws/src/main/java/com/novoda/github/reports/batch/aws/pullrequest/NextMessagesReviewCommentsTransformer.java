package com.novoda.github.reports.batch.aws.pullrequest;

import com.novoda.github.reports.aws.queue.AmazonGetReviewCommentsQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.aws.NextMessagesTransformer;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.network.LastPageExtractor;
import com.novoda.github.reports.service.network.NextPageExtractor;

import java.util.Collections;
import java.util.List;

class NextMessagesReviewCommentsTransformer extends NextMessagesTransformer<RepositoryIssueEvent, AmazonGetReviewCommentsQueueMessage> {

    public static NextMessagesReviewCommentsTransformer newInstance(AmazonGetReviewCommentsQueueMessage currentMessage) {
        NextPageExtractor nextPageExtractor = NextPageExtractor.newInstance();
        LastPageExtractor lastPageExtractor = LastPageExtractor.newInstance();
        return new NextMessagesReviewCommentsTransformer(currentMessage, nextPageExtractor, lastPageExtractor);
    }

    NextMessagesReviewCommentsTransformer(AmazonGetReviewCommentsQueueMessage currentMessage,
                                          NextPageExtractor nextPageExtractor,
                                          LastPageExtractor lastPageExtractor) {

        super(currentMessage, nextPageExtractor, lastPageExtractor);
    }

    @Override
    protected AmazonGetReviewCommentsQueueMessage getNextPageMessage(boolean isTerminalMessage, int nextPage) {
        return AmazonGetReviewCommentsQueueMessage.create(
                isTerminalMessage,
                (long) nextPage,
                currentMessage.receiptHandle(),
                currentMessage.organisationName(),
                currentMessage.sinceOrNull(),
                currentMessage.repositoryId(),
                currentMessage.repositoryName(),
                currentMessage.issueNumber()
        );
    }

    @Override
    protected List<AmazonQueueMessage> getDerivedMessage(RepositoryIssueEvent item) {
        return Collections.emptyList();
    }
}
