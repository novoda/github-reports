package com.novoda.github.reports.lambda.worker;

import com.novoda.github.reports.batch.configuration.Configuration;
import com.novoda.github.reports.batch.aws.queue.AmazonGetCommentsQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonGetEventsQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonGetIssuesQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonGetRepositoriesQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonGetReviewCommentsQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.worker.WorkerHandler;
import com.novoda.github.reports.lambda.MessageNotSupportedException;
import com.novoda.github.reports.lambda.issue.CommentsServiceClient;
import com.novoda.github.reports.lambda.issue.EventsServiceClient;
import com.novoda.github.reports.lambda.issue.IssuesServiceClient;
import com.novoda.github.reports.lambda.pullrequest.ReviewCommentsServiceClient;
import com.novoda.github.reports.lambda.repository.RepositoriesServiceClient;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class AmazonWorkerHandler implements WorkerHandler<AmazonQueueMessage> {

    private final RepositoriesServiceClient repositoriesServiceClient;
    private final IssuesServiceClient issuesServiceClient;
    private final CommentsServiceClient commentsServiceClient;
    private final EventsServiceClient eventsServiceClient;
    private final ReviewCommentsServiceClient reviewCommentsServiceClient;

    public static AmazonWorkerHandler newInstance() {
        RepositoriesServiceClient repositoriesServiceClient = RepositoriesServiceClient.newInstance();
        IssuesServiceClient issuesServiceClient = IssuesServiceClient.newInstance();
        EventsServiceClient eventsServiceClient = EventsServiceClient.newInstance();
        CommentsServiceClient commentsServiceClient = CommentsServiceClient.newInstance();
        ReviewCommentsServiceClient reviewCommentsServiceClient = ReviewCommentsServiceClient.newInstance();
        return new AmazonWorkerHandler(repositoriesServiceClient, issuesServiceClient, eventsServiceClient, commentsServiceClient, reviewCommentsServiceClient);
    }

    private AmazonWorkerHandler(RepositoriesServiceClient repositoriesServiceClient,
                                IssuesServiceClient issuesServiceClient,
                                EventsServiceClient eventsServiceClient,
                                CommentsServiceClient commentsServiceClient,
                                ReviewCommentsServiceClient reviewCommentsServiceClient) {

        this.repositoriesServiceClient = repositoriesServiceClient;
        this.issuesServiceClient = issuesServiceClient;
        this.eventsServiceClient = eventsServiceClient;
        this.commentsServiceClient = commentsServiceClient;
        this.reviewCommentsServiceClient = reviewCommentsServiceClient;
    }

    @Override
    public List<AmazonQueueMessage> handleQueueMessage(Configuration configuration, AmazonQueueMessage queueMessage) throws Throwable {

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
