package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.aws.queue.AmazonGetCommentsQueueMessage;
import com.novoda.github.reports.service.network.LastPageExtractor;
import com.novoda.github.reports.service.network.NextPageExtractor;

class NextMessagesIssueEventCommentTransformer extends NextMessagesIssueEventTransformer<AmazonGetCommentsQueueMessage> {

    public static NextMessagesIssueEventCommentTransformer newInstance(AmazonGetCommentsQueueMessage currentMessage) {
        NextPageExtractor nextPageExtractor = NextPageExtractor.newInstance();
        LastPageExtractor lastPageExtractor = LastPageExtractor.newInstance();
        return new NextMessagesIssueEventCommentTransformer(currentMessage, nextPageExtractor, lastPageExtractor);
    }

    private NextMessagesIssueEventCommentTransformer(AmazonGetCommentsQueueMessage currentMessage,
                                                     NextPageExtractor nextPageExtractor,
                                                     LastPageExtractor lastPageExtractor) {

        super(currentMessage, nextPageExtractor, lastPageExtractor);
    }

    @Override
    protected AmazonGetCommentsQueueMessage getNextPageMessage(boolean isTerminalMessage, int nextPage) {
        return AmazonGetCommentsQueueMessage.create(
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

}
