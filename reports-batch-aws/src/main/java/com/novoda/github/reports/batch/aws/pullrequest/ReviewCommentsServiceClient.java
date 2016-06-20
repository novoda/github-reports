package com.novoda.github.reports.batch.aws.pullrequest;

import com.novoda.github.reports.aws.queue.AmazonGetReviewCommentsQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.queue.QueueMessage;
import com.novoda.github.reports.batch.aws.issue.NextMessagesIssueEventTransformer;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.pullrequest.GithubPullRequestService;
import com.novoda.github.reports.service.pullrequest.PullRequestService;

import rx.Observable;
import rx.functions.Func3;

public class ReviewCommentsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final PullRequestService pullRequestService;
    private final DateToISO8601Converter dateConverter;

    public static ReviewCommentsServiceClient newInstance() {
        PullRequestService pullRequestService = GithubPullRequestService.newInstance();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        return new ReviewCommentsServiceClient(pullRequestService, dateConverter);
    }

    private ReviewCommentsServiceClient(PullRequestService pullRequestService, DateToISO8601Converter dateConverter) {
        this.pullRequestService = pullRequestService;
        this.dateConverter = dateConverter;
    }

    public Observable<AmazonQueueMessage> retrieveReviewCommentsFromPullRequest(AmazonGetReviewCommentsQueueMessage message) {
        String date = dateConverter.toISO8601NoMillisOrNull(message.sinceOrNull());
        return pullRequestService.getPullRequestReviewCommentsFor(message.organisationName(),
                                                                  message.repositoryName(),
                                                                  issueNumberFrom(message),
                                                                  date,
                                                                  pageFrom(message),
                                                                  DEFAULT_PER_PAGE_COUNT)
                .compose(new TransformToRepositoryIssueEvent(message))
                .compose(ResponseRepositoryIssueEventPersistTransformer.newInstance())
                .compose(NextMessagesIssueEventTransformer.newInstance(message, buildAmazonGetReviewCommentsQueueMessage()));
    }

    private int pageFrom(QueueMessage message) {
        return Math.toIntExact(message.page());
    }

    private int issueNumberFrom(AmazonGetReviewCommentsQueueMessage message) {
        return Math.toIntExact(message.issueNumber());
    }

    private Func3<Boolean, Long, AmazonGetReviewCommentsQueueMessage, AmazonGetReviewCommentsQueueMessage> buildAmazonGetReviewCommentsQueueMessage() {
        return (isTerminal, nextPage, getReviewCommentsQueueMessage) -> AmazonGetReviewCommentsQueueMessage.create(
                isTerminal,
                nextPage,
                getReviewCommentsQueueMessage.receiptHandle(),
                getReviewCommentsQueueMessage.organisationName(),
                getReviewCommentsQueueMessage.sinceOrNull(),
                getReviewCommentsQueueMessage.repositoryId(),
                getReviewCommentsQueueMessage.repositoryName(),
                getReviewCommentsQueueMessage.issueNumber()
        );
    }

}
