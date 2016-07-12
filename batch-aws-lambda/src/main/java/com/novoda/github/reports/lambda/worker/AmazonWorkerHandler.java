package com.novoda.github.reports.lambda.worker;

import com.novoda.github.reports.batch.MessageNotSupportedException;
import com.novoda.github.reports.batch.aws.queue.*;
import com.novoda.github.reports.batch.configuration.Configuration;
import com.novoda.github.reports.batch.configuration.DatabaseConfiguration;
import com.novoda.github.reports.batch.logger.Logger;
import com.novoda.github.reports.batch.worker.RetriableNetworkException;
import com.novoda.github.reports.batch.worker.WorkerHandler;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.lambda.issue.CommentsServiceClient;
import com.novoda.github.reports.lambda.issue.EventsServiceClient;
import com.novoda.github.reports.lambda.issue.IssuesServiceClient;
import com.novoda.github.reports.lambda.pullrequest.ReviewCommentsServiceClient;
import com.novoda.github.reports.lambda.repository.RepositoriesServiceClient;
import com.novoda.github.reports.service.network.RateLimitEncounteredException;
import rx.Observable;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

class AmazonWorkerHandler implements WorkerHandler<AmazonQueueMessage> {

    private final Logger logger;

    private RepositoriesServiceClient repositoriesServiceClient;
    private IssuesServiceClient issuesServiceClient;
    private CommentsServiceClient commentsServiceClient;
    private EventsServiceClient eventsServiceClient;
    private ReviewCommentsServiceClient reviewCommentsServiceClient;

    AmazonWorkerHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public List<AmazonQueueMessage> handleQueueMessage(Configuration configuration, AmazonQueueMessage queueMessage)
            throws RateLimitEncounteredException, MessageNotSupportedException, RetriableNetworkException {

        init(configuration);

        Observable<AmazonQueueMessage> nextMessagesObservable;

        if (queueMessage instanceof AmazonGetRepositoriesQueueMessage) {
            AmazonGetRepositoriesQueueMessage message = (AmazonGetRepositoriesQueueMessage) queueMessage;
            nextMessagesObservable = repositoriesServiceClient.retrieveRepositoriesFor(message);
        } else if (queueMessage instanceof AmazonGetIssuesQueueMessage) {
            AmazonGetIssuesQueueMessage message = (AmazonGetIssuesQueueMessage) queueMessage;
            nextMessagesObservable = issuesServiceClient.retrieveIssuesFor(message);
        } else if (queueMessage instanceof AmazonGetEventsQueueMessage) {
            AmazonGetEventsQueueMessage message = (AmazonGetEventsQueueMessage) queueMessage;
            nextMessagesObservable = eventsServiceClient.retrieveEventsFrom(message);
        } else if (queueMessage instanceof AmazonGetCommentsQueueMessage) {
            AmazonGetCommentsQueueMessage message = (AmazonGetCommentsQueueMessage) queueMessage;
            nextMessagesObservable = commentsServiceClient.retrieveCommentsAsEventsFrom(message);
        } else if (queueMessage instanceof AmazonGetReviewCommentsQueueMessage) {
            AmazonGetReviewCommentsQueueMessage message = (AmazonGetReviewCommentsQueueMessage) queueMessage;
            nextMessagesObservable = reviewCommentsServiceClient.retrieveReviewCommentsFromPullRequest(message);
        } else {
            throw new MessageNotSupportedException(queueMessage);
        }

        List<AmazonQueueMessage> nextMessages = collectDerivedMessagesFrom(nextMessagesObservable);
        return nextMessages;
    }

    private void init(Configuration configuration) {
        DatabaseCredentialsReader databaseCredentialsReader = buildDatabaseCredentialsReader(configuration);

        repositoriesServiceClient = RepositoriesServiceClient.newInstance(databaseCredentialsReader);
        issuesServiceClient = IssuesServiceClient.newInstance(databaseCredentialsReader);
        eventsServiceClient = EventsServiceClient.newInstance(databaseCredentialsReader);
        commentsServiceClient = CommentsServiceClient.newInstance(databaseCredentialsReader);
        reviewCommentsServiceClient = ReviewCommentsServiceClient.newInstance(databaseCredentialsReader);
    }

    private DatabaseCredentialsReader buildDatabaseCredentialsReader(Configuration configuration) {
        Properties databaseProperties = new Properties();
        DatabaseConfiguration databaseConfiguration = configuration.databaseConfiguration();
        databaseProperties.setProperty(DatabaseCredentialsReader.USER_KEY, databaseConfiguration.username());
        databaseProperties.setProperty(DatabaseCredentialsReader.PASSWORD_KEY, databaseConfiguration.password());
        databaseProperties.setProperty(DatabaseCredentialsReader.CONNECTION_STRING_KEY, databaseConfiguration.connectionString());
        return DatabaseCredentialsReader.newInstance(databaseProperties);
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
