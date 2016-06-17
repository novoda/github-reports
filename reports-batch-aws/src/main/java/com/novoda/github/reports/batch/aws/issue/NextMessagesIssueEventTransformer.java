package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.aws.NextMessagesTransformer;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.network.LastPageExtractor;
import com.novoda.github.reports.service.network.NextPageExtractor;

import java.util.Collections;
import java.util.List;

import rx.functions.Func3;

class NextMessagesIssueEventTransformer<M extends AmazonQueueMessage> extends NextMessagesTransformer<RepositoryIssueEvent, M> {

    private final Func3<Boolean, Long, M, M> amazonQueueMessageCreator;

    public static <M extends AmazonQueueMessage> NextMessagesTransformer<RepositoryIssueEvent, M> newInstance(
            M currentMessage,
            Func3<Boolean, Long, M, M> amazonQueueMessageCreator) {

        NextPageExtractor nextPageExtractor = NextPageExtractor.newInstance();
        LastPageExtractor lastPageExtractor = LastPageExtractor.newInstance();

        return new NextMessagesIssueEventTransformer<>(currentMessage, nextPageExtractor, lastPageExtractor, amazonQueueMessageCreator);
    }

    private NextMessagesIssueEventTransformer(M currentMessage,
                                      NextPageExtractor nextPageExtractor,
                                      LastPageExtractor lastPageExtractor,
                                      Func3<Boolean, Long, M, M> amazonQueueMessageCreator) {

        super(currentMessage, nextPageExtractor, lastPageExtractor);
        this.amazonQueueMessageCreator = amazonQueueMessageCreator;
    }

    @Override
    protected M getNextPageMessage(boolean isTerminalMessage, int nextPage) {
        return amazonQueueMessageCreator.call(
                isTerminalMessage,
                (long) nextPage,
                currentMessage
        );
    }

    @Override
    protected List<AmazonQueueMessage> getDerivedMessage(RepositoryIssueEvent item) {
        return Collections.emptyList();
    }

}
