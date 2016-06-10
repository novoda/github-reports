package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.batch.aws.pullrequest.PullRequestServiceClient;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.RepositoryIssue;

import java.util.Date;

import rx.Observable;

public class ReviewCommentsServiceClient {

    private final PullRequestServiceClient pullRequestServiceClient;

    public static ReviewCommentsServiceClient newInstance() {
        PullRequestServiceClient pullRequestServiceClient = PullRequestServiceClient.newInstance();
        return new ReviewCommentsServiceClient(pullRequestServiceClient);
    }

    private ReviewCommentsServiceClient(PullRequestServiceClient pullRequestServiceClient) {
        this.pullRequestServiceClient = pullRequestServiceClient;
    }

    public Observable<GithubComment> retrieveReviewCommentsFromPullRequest(RepositoryIssue repositoryIssue, Date since, int page) {
        if (isNotPullRequest(repositoryIssue)) {
            return Observable.empty();
        }
        String organisation = repositoryIssue.getOwnerUsername();
        String repository = repositoryIssue.getRepositoryName();
        int issueNumber = repositoryIssue.getIssueNumber();
        return pullRequestServiceClient.getPullRequestReviewCommentsFor(organisation, repository, issueNumber, since, page);
    }

    private boolean isNotPullRequest(RepositoryIssue repositoryIssue) {
        return !repositoryIssue.isPullRequest();
    }

}
