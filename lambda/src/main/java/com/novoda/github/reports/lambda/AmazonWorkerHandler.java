package com.novoda.github.reports.lambda;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.queue.AmazonGetCommentsQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetEventsQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetIssuesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetRepositoriesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetReviewCommentsQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.worker.WorkerHandler;
import com.novoda.github.reports.batch.aws.issue.IssuesServiceClient;
import com.novoda.github.reports.batch.aws.repository.RepositoriesServiceClient;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class AmazonWorkerHandler implements WorkerHandler<AmazonQueueMessage> {

    private final RepositoriesServiceClient repositoriesServiceClient;
    private final IssuesServiceClient issuesServiceClient;

    public static AmazonWorkerHandler newInstance() {
        RepositoriesServiceClient repositoriesServiceClient = RepositoriesServiceClient.newInstance();
        IssuesServiceClient issuesServiceClient = IssuesServiceClient.newInstance();
        return new AmazonWorkerHandler(repositoriesServiceClient, issuesServiceClient);
    }

    AmazonWorkerHandler(RepositoriesServiceClient repositoriesServiceClient, IssuesServiceClient issuesServiceClient) {
        this.repositoriesServiceClient = repositoriesServiceClient;
        this.issuesServiceClient = issuesServiceClient;
    }

    @Override
    public List<AmazonQueueMessage> handleQueueMessage(Configuration configuration, AmazonQueueMessage queueMessage) throws Throwable {

        Observable<AmazonQueueMessage> nextMessagesObservable = Observable.empty();

        if (queueMessage instanceof AmazonGetRepositoriesQueueMessage) {
            AmazonGetRepositoriesQueueMessage message = (AmazonGetRepositoriesQueueMessage) queueMessage;
            nextMessagesObservable = repositoriesServiceClient.getRepositoriesFor(message);
        } else if (queueMessage instanceof AmazonGetIssuesQueueMessage) {
            AmazonGetIssuesQueueMessage message = (AmazonGetIssuesQueueMessage) queueMessage;
            nextMessagesObservable = issuesServiceClient.retrieveIssuesFor(message);
        } else if (queueMessage instanceof AmazonGetEventsQueueMessage) {
            AmazonGetEventsQueueMessage message = (AmazonGetEventsQueueMessage) queueMessage;
            // TODO
        } else if (queueMessage instanceof AmazonGetCommentsQueueMessage) {
            AmazonGetCommentsQueueMessage message = (AmazonGetCommentsQueueMessage) queueMessage;
            // TODO
        } else if (queueMessage instanceof AmazonGetReviewCommentsQueueMessage) {
            AmazonGetReviewCommentsQueueMessage message = (AmazonGetReviewCommentsQueueMessage) queueMessage;
            // TODO
        } else {
            throw new MessageNotSupportedException(queueMessage);
        }

        try {
            return collectDerivedMessagesFrom(nextMessagesObservable);
        } catch (RuntimeException exception) {
            Throwable cause = exception.getCause();
            if (cause != null) {
                throw cause;
            }
            throw exception;
        }

    }

    private ArrayList<AmazonQueueMessage> collectDerivedMessagesFrom(Observable<AmazonQueueMessage> nextMessagesObservable) {
        return nextMessagesObservable
                .collect(ArrayList<AmazonQueueMessage>::new, ArrayList::add)
                .toBlocking()
                .first();
    }

}
