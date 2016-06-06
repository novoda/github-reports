package com.novoda.github.reports.batch.pullrequest;

import com.novoda.github.reports.batch.issue.Comment;

import rx.Observable;

public class GithubPullRequestService implements PullRequestService {

    @Override
    public Observable<Comment> getReviewCommentsForPullRequestFor(String organisation, String repository, Integer pullRequestNumber) {
        return null;
    }
}
