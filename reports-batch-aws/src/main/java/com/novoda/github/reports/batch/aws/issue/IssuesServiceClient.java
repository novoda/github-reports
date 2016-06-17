package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.aws.queue.AmazonGetIssuesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.queue.QueueMessage;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.repository.GithubRepository;

import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Headers;
import retrofit2.Response;
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
                .compose(new TransformToRepositoryIssue(message.repositoryId()))
                .compose(ResponseRepositoryIssuePersistTransformer.newInstance())
                .compose(NextMessagesIssueTransformer.newInstance(message));
    }

    private int pageFrom(QueueMessage message) {
        return Math.toIntExact(message.page());
    }

    private class TransformToRepositoryIssue implements Observable.Transformer<Response<List<GithubIssue>>, Response<List<RepositoryIssue>>> {
        private final Long repositoryId;

        TransformToRepositoryIssue(Long repositoryId) {
            this.repositoryId = repositoryId;
        }

        @Override
        public Observable<Response<List<RepositoryIssue>>> call(Observable<Response<List<GithubIssue>>> responseObservable) {
            return responseObservable
                    .map(response -> {
                        Headers headers = response.headers();
                        List<RepositoryIssue> body = response.body().stream()
                                .map(githubIssue -> new RepositoryIssue(new GithubRepository(repositoryId), githubIssue))
                                .collect(Collectors.toList());
                        return Response.success(body, headers);
                    });
        }
    }

}
