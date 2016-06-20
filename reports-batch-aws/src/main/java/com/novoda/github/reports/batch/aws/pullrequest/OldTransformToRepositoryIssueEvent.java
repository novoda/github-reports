package com.novoda.github.reports.batch.aws.pullrequest;

import com.novoda.github.reports.aws.queue.AmazonGetReviewCommentsQueueMessage;
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

@Deprecated
class OldTransformToRepositoryIssueEvent implements Observable.Transformer<Response<List<GithubComment>>, Response<List<RepositoryIssueEvent>>> {

    private long repositoryId;
    private int issueNumber;

    OldTransformToRepositoryIssueEvent(AmazonGetReviewCommentsQueueMessage message) {
        this.repositoryId = message.repositoryId();
        this.issueNumber = Math.toIntExact(message.issueNumber());
    }

    @Override
    public Observable<Response<List<RepositoryIssueEvent>>> call(Observable<Response<List<GithubComment>>> responseObservable) {
        return responseObservable.map(response -> {
            Headers headers = response.headers();
            List<RepositoryIssueEvent> body = response.body().stream()
                    .map(githubComment -> new RepositoryIssueEventComment(getRepositoryIssue(), githubComment))
                    .collect(Collectors.toList());
            return Response.success(body, headers);
        });
    }

    private RepositoryIssue getRepositoryIssue() {
        return new RepositoryIssue(new GithubRepository(repositoryId), new GithubIssue(issueNumber));
    }
}
