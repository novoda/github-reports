package com.novoda.github.reports.lambda.issue;

import com.novoda.github.reports.aws.queue.AmazonGetIssuesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.queue.QueueMessage;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.network.DateToISO8601Converter;

import rx.Observable;

public class IssuesServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final GithubIssue.State DEFAULT_STATE = GithubIssue.State.ALL;

    private final IssueService issueService;
    private final DateToISO8601Converter dateConverter;

    public static IssuesServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        return new IssuesServiceClient(issueService, dateConverter);
    }

    private IssuesServiceClient(IssueService issueService, DateToISO8601Converter dateConverter) {
        this.issueService = issueService;
        this.dateConverter = dateConverter;
    }

    public Observable<AmazonQueueMessage> retrieveIssuesFor(AmazonGetIssuesQueueMessage message) {
        String date = dateConverter.toISO8601NoMillisOrNull(message.sinceOrNull());
        return issueService
                .getIssuesFor(
                        message.organisationName(),
                        message.repositoryName(),
                        DEFAULT_STATE,
                        date,
                        pageFrom(message),
                        DEFAULT_PER_PAGE_COUNT
                )
                .compose(TransformToRepositoryIssue.newInstance(message.repositoryId()))
                .compose(ResponseRepositoryIssuePersistTransformer.newInstance())
                .compose(NextMessagesIssueTransformer.newInstance(message));
    }

    private int pageFrom(QueueMessage message) {
        return Math.toIntExact(message.page());
    }

}
