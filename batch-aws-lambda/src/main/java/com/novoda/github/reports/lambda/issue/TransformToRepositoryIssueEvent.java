package com.novoda.github.reports.lambda.issue;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.repository.GithubRepository;

import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Headers;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Func2;

public class TransformToRepositoryIssueEvent<T, C extends RepositoryIssueEvent>
        implements Observable.Transformer<Response<List<T>>, Response<List<RepositoryIssueEvent>>> {

    private final Long repositoryId;
    private final Long issueNumber;
    private final Long issueOwnerId;
    private final boolean isPullRequest;
    private final Func2<RepositoryIssue, T, C> repositoryIssueEventCreator;

    public TransformToRepositoryIssueEvent(Long repositoryId,
                                           Long issueNumber,
                                           Long issueOwnerId,
                                           boolean isPullRequest,
                                           Func2<RepositoryIssue, T, C> repositoryIssueEventCreator) {

        this.repositoryId = repositoryId;
        this.issueNumber = issueNumber;
        this.issueOwnerId = issueOwnerId;
        this.isPullRequest = isPullRequest;
        this.repositoryIssueEventCreator = repositoryIssueEventCreator;
    }

    @Override
    public Observable<Response<List<RepositoryIssueEvent>>> call(Observable<Response<List<T>>> responseObservable) {
        return responseObservable
                .map(response -> {
                    Headers headers = response.headers();
                    List<RepositoryIssueEvent> body = response.body().stream()
                            .map(githubEvent -> {
                                GithubRepository repository = new GithubRepository(repositoryId);
                                GithubIssue issue = new GithubIssue(Math.toIntExact(issueNumber), issueOwnerId, isPullRequest);
                                RepositoryIssue repositoryIssue = new RepositoryIssue(repository, issue);
                                return repositoryIssueEventCreator.call(repositoryIssue, githubEvent);
                            })
                            .collect(Collectors.toList());
                    return Response.success(body, headers);
                });
    }
}
