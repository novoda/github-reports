package com.novoda.github.reports.lambda;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.queue.AmazonGetRepositoriesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.worker.WorkerHandler;
import com.novoda.github.reports.batch.aws.repository.RepositoriesServiceClient;
import com.novoda.github.reports.service.network.RateLimitEncounteredException;
import com.novoda.github.reports.service.repository.GithubRepository;

import java.util.Collections;
import java.util.List;

import retrofit2.Response;
import rx.Subscriber;

public class AmazonHandler implements WorkerHandler<AmazonQueueMessage> {

    private final RepositoriesServiceClient repositoriesServiceClient = RepositoriesServiceClient.newInstance();

    @Override
    public List<AmazonQueueMessage> handleQueueMessage(Configuration configuration, AmazonQueueMessage queueMessage)
            throws RateLimitEncounteredException, Exception {

        // if there's no more stuff to do return an empty list

        if (queueMessage instanceof AmazonGetRepositoriesQueueMessage) {
            AmazonGetRepositoriesQueueMessage message = (AmazonGetRepositoriesQueueMessage) queueMessage;

            // - check if we're the last page, if so issue request and return empty list
            if (message.localTerminal()) {
                getRepositories(message);
                return noNewMessage();
            }

            // - if we're not the last page issue the request and ... ?


        } // ...

        return null;
    }

    private List<AmazonQueueMessage> noNewMessage() {
        return Collections.emptyList();
    }

    private void getRepositories(AmazonGetRepositoriesQueueMessage message) throws RateLimitEncounteredException, Exception {
        repositoriesServiceClient.getRepositoriesResponseFor(message.organisationName(), Math.toIntExact(message.page()))
                .subscribe(new Subscriber<Response<List<GithubRepository>>>() {
                    @Override
                    public void onCompleted() {
                        // TODO
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO
                    }

                    @Override
                    public void onNext(Response<List<GithubRepository>> response) {
                        // TODO extract next to last pages



                        // TODO create new messages for each page and queue them
                    }
                });
    }
}
