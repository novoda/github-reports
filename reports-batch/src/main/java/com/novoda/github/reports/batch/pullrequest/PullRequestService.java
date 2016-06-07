package com.novoda.github.reports.batch.pullrequest;

import com.novoda.github.reports.batch.issue.GithubComment;

import java.util.Date;

import rx.Observable;

public interface PullRequestService {

    Observable<GithubComment> getReviewCommentsForPullRequestFor(String organisation, String repository, Integer pullRequestNumber, Date since);

}
