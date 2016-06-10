package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.pullrequest.PullRequestServiceClient;
import com.novoda.github.reports.batch.retry.RateLimitResetTimerSubject;
import com.novoda.github.reports.batch.retry.RateLimitResetTimerSubjectContainer;
import com.novoda.github.reports.batch.retry.RetryWhenTokenResets;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.RepositoryIssue;

import java.util.Date;

import rx.Observable;

class ReviewCommentsServiceClient {

    private final PullRequestServiceClient pullRequestServiceClient;
    private final RateLimitResetTimerSubject rateLimitResetTimerSubject;

    public static ReviewCommentsServiceClient newInstance() {
        PullRequestServiceClient pullRequestServiceClient = PullRequestServiceClient.newInstance();
        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();
        return new ReviewCommentsServiceClient(pullRequestServiceClient, rateLimitResetTimerSubject);
    }

    private ReviewCommentsServiceClient(PullRequestServiceClient pullRequestServiceClient, RateLimitResetTimerSubject rateLimitResetTimerSubject) {
        this.pullRequestServiceClient = pullRequestServiceClient;
        this.rateLimitResetTimerSubject = rateLimitResetTimerSubject;
    }

    Observable<GithubComment> retrieveReviewCommentsFromPullRequest(RepositoryIssue repositoryIssue, Date since) {
        if (isNotPullRequest(repositoryIssue)) {
            return Observable.empty();
        }
        String organisation = repositoryIssue.getOwnerUsername();
        String repository = repositoryIssue.getRepositoryName();
        int issueNumber = repositoryIssue.getIssueNumber();
        return pullRequestServiceClient
                .getPullRequestReviewCommentsFor(organisation, repository, issueNumber, since)
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject));
    }

    private boolean isNotPullRequest(RepositoryIssue repositoryIssue) {
        return !repositoryIssue.isPullRequest();
    }
    
}
