package com.novoda.github.reports.lambda.worker;

import com.novoda.github.reports.batch.MessageNotSupportedException;
import com.novoda.github.reports.batch.aws.queue.*;
import com.novoda.github.reports.batch.configuration.Configuration;
import com.novoda.github.reports.batch.configuration.DatabaseConfiguration;
import com.novoda.github.reports.batch.configuration.GithubConfiguration;
import com.novoda.github.reports.batch.logger.Logger;
import com.novoda.github.reports.batch.worker.RetriableNetworkException;
import com.novoda.github.reports.batch.worker.WorkerHandler;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.lambda.issue.CommentsServiceClient;
import com.novoda.github.reports.lambda.issue.EventsServiceClient;
import com.novoda.github.reports.lambda.issue.IssuesServiceClient;
import com.novoda.github.reports.lambda.issue.ReactionsServiceClient;
import com.novoda.github.reports.lambda.pullrequest.ReviewCommentsServiceClient;
import com.novoda.github.reports.lambda.repository.RepositoriesServiceClient;
import com.novoda.github.reports.service.network.GithubApiService;
import com.novoda.github.reports.service.network.GithubServiceContainer;
import com.novoda.github.reports.service.network.RateLimitEncounteredException;
import com.novoda.github.reports.service.properties.GithubCredentialsReader;
import rx.Observable;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

class AmazonWorkerHandler implements WorkerHandler<AmazonQueueMessage> {

    private final Logger logger;

    private DatabaseCredentialsReader databaseCredentialsReader;
    private GithubApiService githubApiService;

    AmazonWorkerHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public List<AmazonQueueMessage> handleQueueMessage(Configuration configuration, AmazonQueueMessage queueMessage)
            throws RateLimitEncounteredException, MessageNotSupportedException, RetriableNetworkException {

        init(configuration);

        Observable<AmazonQueueMessage> nextMessagesObservable;

        if (queueMessage instanceof AmazonGetRepositoriesQueueMessage) {
            nextMessagesObservable = handleGetRepositoriesQueueMessage(queueMessage);
        } else if (queueMessage instanceof AmazonGetIssuesQueueMessage) {
            nextMessagesObservable = handleGetIssuesQueueMessage(queueMessage);
        } else if (queueMessage instanceof AmazonGetEventsQueueMessage) {
            nextMessagesObservable = handleGetEventsQueueMessage(queueMessage);
        } else if (queueMessage instanceof AmazonGetCommentsQueueMessage) {
            nextMessagesObservable = handleGetCommentsQueueMessage(queueMessage);
        } else if (queueMessage instanceof AmazonGetReviewCommentsQueueMessage) {
            nextMessagesObservable = handleGetReviewCommentsQueueMessage(queueMessage);
        } else if (queueMessage instanceof AmazonGetReactionsQueueMessage) {
            nextMessagesObservable = handleGetReactionsQueueMessage(queueMessage);
        } else {
            throw new MessageNotSupportedException(queueMessage);
        }

        return collectDerivedMessagesFrom(nextMessagesObservable);
    }

    private void init(Configuration configuration) {
        databaseCredentialsReader = buildDatabaseCredentialsReader(configuration);
        GithubCredentialsReader githubCredentialsReader = buildGithubCredentialsReader(configuration);
        githubApiService = GithubServiceContainer.getGithubService(githubCredentialsReader);
    }

    private DatabaseCredentialsReader buildDatabaseCredentialsReader(Configuration configuration) {
        Properties databaseProperties = new Properties();
        DatabaseConfiguration databaseConfiguration = configuration.databaseConfiguration();
        databaseProperties.setProperty(DatabaseCredentialsReader.USER_KEY, databaseConfiguration.username());
        databaseProperties.setProperty(DatabaseCredentialsReader.PASSWORD_KEY, databaseConfiguration.password());
        databaseProperties.setProperty(DatabaseCredentialsReader.CONNECTION_STRING_KEY, databaseConfiguration.connectionString());
        return DatabaseCredentialsReader.newInstance(databaseProperties);
    }

    private GithubCredentialsReader buildGithubCredentialsReader(Configuration configuration) {
        Properties githubProperties = new Properties();
        GithubConfiguration githubConfiguration = configuration.githubConfiguration();
        githubProperties.setProperty(GithubCredentialsReader.TOKEN_KEY, githubConfiguration.token());
        return GithubCredentialsReader.newInstance(githubProperties);
    }


    private Observable<AmazonQueueMessage> handleGetRepositoriesQueueMessage(AmazonQueueMessage queueMessage) {
        AmazonGetRepositoriesQueueMessage message = (AmazonGetRepositoriesQueueMessage) queueMessage;
        RepositoriesServiceClient repositoriesServiceClient = RepositoriesServiceClient.newInstance(
                githubApiService,
                databaseCredentialsReader
        );

        return repositoriesServiceClient.retrieveRepositoriesFor(message);
    }

    private Observable<AmazonQueueMessage> handleGetIssuesQueueMessage(AmazonQueueMessage queueMessage) {
        AmazonGetIssuesQueueMessage message = (AmazonGetIssuesQueueMessage) queueMessage;
        IssuesServiceClient issuesServiceClient = IssuesServiceClient.newInstance(
                githubApiService,
                databaseCredentialsReader
        );

        return issuesServiceClient.retrieveIssuesFor(message);
    }

    private Observable<AmazonQueueMessage> handleGetEventsQueueMessage(AmazonQueueMessage queueMessage) {
        AmazonGetEventsQueueMessage message = (AmazonGetEventsQueueMessage) queueMessage;
        EventsServiceClient eventsServiceClient = EventsServiceClient.newInstance(
                githubApiService,
                databaseCredentialsReader
        );

        return eventsServiceClient.retrieveEventsFrom(message);
    }

    private Observable<AmazonQueueMessage> handleGetCommentsQueueMessage(AmazonQueueMessage queueMessage) {
        AmazonGetCommentsQueueMessage message = (AmazonGetCommentsQueueMessage) queueMessage;
        CommentsServiceClient commentsServiceClient = CommentsServiceClient.newInstance(
                githubApiService,
                databaseCredentialsReader
        );

        return commentsServiceClient.retrieveCommentsAsEventsFrom(message);
    }

    private Observable<AmazonQueueMessage> handleGetReviewCommentsQueueMessage(AmazonQueueMessage queueMessage) {
        AmazonGetReviewCommentsQueueMessage message = (AmazonGetReviewCommentsQueueMessage) queueMessage;
        ReviewCommentsServiceClient reviewCommentsServiceClient = ReviewCommentsServiceClient.newInstance(
                githubApiService,
                databaseCredentialsReader
        );

        return reviewCommentsServiceClient.retrieveReviewCommentsFromPullRequest(message);
    }

    private Observable<AmazonQueueMessage> handleGetReactionsQueueMessage(AmazonQueueMessage queueMessage) {
        AmazonGetReactionsQueueMessage message = (AmazonGetReactionsQueueMessage) queueMessage;
        ReactionsServiceClient reactionsServiceClient = ReactionsServiceClient.newInstance(
                githubApiService,
                databaseCredentialsReader
        );

        return reactionsServiceClient.retrieveReactionsAsEventsFrom(message);
    }
    private ArrayList<AmazonQueueMessage> collectDerivedMessagesFrom(Observable<AmazonQueueMessage> nextMessagesObservable)
            throws RateLimitEncounteredException, RetriableNetworkException {

        try {
            return nextMessagesObservable
                    .collect(ArrayList<AmazonQueueMessage>::new, ArrayList::add)
                    .toBlocking()
                    .first();
        } catch (RuntimeException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof SocketTimeoutException) {
                throw new RetriableNetworkException(cause);
            }
            throw exception;
        }
    }

}
