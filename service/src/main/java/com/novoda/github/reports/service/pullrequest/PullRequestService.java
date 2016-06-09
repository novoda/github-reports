package com.novoda.github.reports.service.pullrequest;

import com.novoda.github.reports.service.issue.GithubComment;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

interface PullRequestService {

    Observable<Response<List<GithubComment>>> getPullRequestReviewCommentsFor(String organisation,
                                                                              String repository,
                                                                              int pullRequestNumber,
                                                                              String since,
                                                                              int page,
                                                                              int pageCount);

}
