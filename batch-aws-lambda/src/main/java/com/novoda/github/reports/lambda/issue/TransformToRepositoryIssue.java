package com.novoda.github.reports.lambda.issue;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.repository.GithubRepository;

import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Headers;
import retrofit2.Response;
import rx.Observable;

class TransformToRepositoryIssue implements Observable.Transformer<Response<List<GithubIssue>>, Response<List<RepositoryIssue>>> {

    private final Long repositoryId;

    public static TransformToRepositoryIssue newInstance(Long repositoryId) {
        return new TransformToRepositoryIssue(repositoryId);
    }

    private TransformToRepositoryIssue(Long repositoryId) {
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
