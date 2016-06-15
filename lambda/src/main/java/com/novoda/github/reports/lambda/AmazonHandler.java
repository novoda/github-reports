package com.novoda.github.reports.lambda;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.queue.AmazonGetCommentsQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetEventsQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetIssuesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetRepositoriesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetReviewCommentsQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.worker.WorkerHandler;
import com.novoda.github.reports.batch.aws.repository.RepositoriesServiceClient;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class AmazonHandler implements WorkerHandler<AmazonQueueMessage> {

    private final RepositoriesServiceClient repositoriesServiceClient;

    public static AmazonHandler newInstance() {
        RepositoriesServiceClient repositoriesServiceClient = RepositoriesServiceClient.newInstance();
        return new AmazonHandler(repositoriesServiceClient);
    }

    AmazonHandler(RepositoriesServiceClient repositoriesServiceClient) {
        this.repositoriesServiceClient = repositoriesServiceClient;
    }

    @Override
    public List<AmazonQueueMessage> handleQueueMessage(Configuration configuration, AmazonQueueMessage queueMessage) throws Throwable {

        Observable<AmazonQueueMessage> nextMessagesObservable = Observable.empty();

        if (queueMessage instanceof AmazonGetRepositoriesQueueMessage) {
            AmazonGetRepositoriesQueueMessage message = (AmazonGetRepositoriesQueueMessage) queueMessage;
            nextMessagesObservable = repositoriesServiceClient.getRepositoriesFor(message);
        } else if (queueMessage instanceof AmazonGetIssuesQueueMessage) {
            // TODO
        } else if (queueMessage instanceof AmazonGetEventsQueueMessage) {
            // TODO
        } else if (queueMessage instanceof AmazonGetCommentsQueueMessage) {
            // TODO
        } else if (queueMessage instanceof AmazonGetReviewCommentsQueueMessage) {
            // TODO
        } else {
            throw new IllegalArgumentException("QueueMessage \"" + queueMessage.getClass().getSimpleName() + "\" not supported.");
        }

        try {
            return nextMessagesObservable
                    .collect(ArrayList<AmazonQueueMessage>::new, ArrayList::add)
                    .toBlocking()
                    .first();
        } catch (RuntimeException exception) {
            Throwable cause = exception.getCause();
            if (cause != null) {
                throw cause;
            }
            throw exception;
        }

    }

}
