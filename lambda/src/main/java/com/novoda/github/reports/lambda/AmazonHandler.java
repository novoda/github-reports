package com.novoda.github.reports.lambda;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.queue.AmazonGetRepositoriesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.worker.WorkerHandler;
import com.novoda.github.reports.batch.aws.repository.RepositoriesServiceClient;
import com.novoda.github.reports.service.network.RateLimitEncounteredException;

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
    public List<AmazonQueueMessage> handleQueueMessage(Configuration configuration, AmazonQueueMessage queueMessage)
            throws RateLimitEncounteredException, Exception {

        Observable<AmazonQueueMessage> nextMessagesObservable = Observable.empty();

        if (queueMessage instanceof AmazonGetRepositoriesQueueMessage) {
            AmazonGetRepositoriesQueueMessage message = (AmazonGetRepositoriesQueueMessage) queueMessage;
            nextMessagesObservable = repositoriesServiceClient.getRepositoriesFor(message);
        }
        // TODO other messages ...

        return nextMessagesObservable
                .collect(ArrayList<AmazonQueueMessage>::new, ArrayList::add)
                .toBlocking()
                .single();
    }

}
