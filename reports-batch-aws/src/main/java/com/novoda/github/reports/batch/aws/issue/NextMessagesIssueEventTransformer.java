package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.aws.NextMessagesTransformer;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.network.LastPageExtractor;
import com.novoda.github.reports.service.network.NextPageExtractor;

import java.util.Collections;
import java.util.List;

abstract class NextMessagesIssueEventTransformer<M extends AmazonQueueMessage> extends NextMessagesTransformer<RepositoryIssueEvent, M> {

    NextMessagesIssueEventTransformer(M currentMessage, NextPageExtractor nextPageExtractor, LastPageExtractor lastPageExtractor) {
        super(currentMessage, nextPageExtractor, lastPageExtractor);
    }

    @Override
    protected List<AmazonQueueMessage> getDerivedMessage(RepositoryIssueEvent item) {
        return Collections.emptyList();
    }

}
