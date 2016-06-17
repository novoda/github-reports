package com.novoda.github.reports.batch.aws.pullrequest;

import com.novoda.github.reports.aws.queue.AmazonGetReviewCommentsQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.queue.QueueMessage;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.persistence.RepositoryIssueEventPersistTransformer;
import com.novoda.github.reports.service.pullrequest.PullRequestService;

import java.util.Date;

import rx.Observable;

public class ReviewCommentsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final PullRequestService pullRequestService;
    private final DateToISO8601Converter dateConverter;
    private final RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer;

    public ReviewCommentsServiceClient(PullRequestService pullRequestService,
                                       DateToISO8601Converter dateConverter,
                                       RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer) {

        this.pullRequestService = pullRequestService;
        this.dateConverter = dateConverter;
        this.repositoryIssueEventPersistTransformer = repositoryIssueEventPersistTransformer;
    }

    @Deprecated
    public Observable<RepositoryIssueEvent> retrieveReviewCommentsFromPullRequest(RepositoryIssue repositoryIssue, Date since, int page) {
        if (isNotPullRequest(repositoryIssue)) {
            return Observable.empty();
        }
        String organisation = repositoryIssue.getOwnerUsername();
        String repository = repositoryIssue.getRepositoryName();
        int issueNumber = repositoryIssue.getIssueNumber();
        //return pullRequestServiceClient.getPullRequestReviewCommentsFor(organisation, repository, issueNumber, since, page)
        //        .map(comment -> new RepositoryIssueEventComment(repositoryIssue, comment))
        //        .compose(repositoryIssueEventPersistTransformer);
        return Observable.empty();
    }

    private boolean isNotPullRequest(RepositoryIssue repositoryIssue) {
        return !repositoryIssue.isPullRequest();
    }

    public Observable<AmazonQueueMessage> retrieveReviewCommentsFromPullRequest(AmazonGetReviewCommentsQueueMessage message) {
        // TODO

        String date = dateConverter.toISO8601NoMillisOrNull(message.sinceOrNull());
        Observable o =
                pullRequestService.getPullRequestReviewCommentsFor(
                        message.organisationName(),
                        message.repositoryName(),
                        issueNumberFrom(message),
                        date,
                        pageFrom(message),
                        DEFAULT_PER_PAGE_COUNT
                )
                        .compose(new TransformToRepositoryIssueEvent(message))
                        .compose(ResponseRepositoryIssueEventPersistTransformer.newInstance())
                //.compose(NextMessagesReviewCommentsTransformer.newInstance(message))
                ;

        return Observable.empty();
    }

    private int pageFrom(QueueMessage message) {
        return Math.toIntExact(message.page());
    }

    private int issueNumberFrom(AmazonGetReviewCommentsQueueMessage message) {
        return Math.toIntExact(message.issueNumber());
    }

}
