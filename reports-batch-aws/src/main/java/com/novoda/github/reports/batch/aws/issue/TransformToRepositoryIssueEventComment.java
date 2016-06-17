package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventComment;
import com.novoda.github.reports.service.repository.GithubRepository;

import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Headers;
import retrofit2.Response;
import rx.Observable;

class TransformToRepositoryIssueEventComment
        implements Observable.Transformer<Response<List<GithubComment>>, Response<List<RepositoryIssueEvent>>> {

    private final Long repositoryId;
    private final Long issueNumber;

    public static TransformToRepositoryIssueEventComment newInstance(Long repositoryId, Long issueNumber) {
        return new TransformToRepositoryIssueEventComment(repositoryId, issueNumber);
    }

    private TransformToRepositoryIssueEventComment(Long repositoryId, Long issueNumber) {
        this.repositoryId = repositoryId;
        this.issueNumber = issueNumber;
    }

    @Override
    public Observable<Response<List<RepositoryIssueEvent>>> call(Observable<Response<List<GithubComment>>> responseObservable) {
        return responseObservable
                .map(response -> {
                    Headers headers = response.headers();
                    List<RepositoryIssueEvent> body = response.body().stream()
                            .map(githubComment -> {
                                GithubRepository repository = new GithubRepository(repositoryId);
                                GithubIssue issue = new GithubIssue(Math.toIntExact(issueNumber));
                                RepositoryIssue repositoryIssue = new RepositoryIssue(repository, issue);
                                return new RepositoryIssueEventComment(repositoryIssue, githubComment);
                            })
                            .collect(Collectors.toList());
                    return Response.success(body, headers);
                });
    }
}
