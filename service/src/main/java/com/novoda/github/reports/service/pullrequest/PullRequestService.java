package com.novoda.github.reports.service.pullrequest;

import com.novoda.github.reports.service.issue.GithubComment;

import java.util.Date;

import rx.Observable;

public interface PullRequestService {

    Observable<GithubComment> getReviewCommentsForPullRequestFor(String organisation, String repository, Integer pullRequestNumber, Date since);

}
