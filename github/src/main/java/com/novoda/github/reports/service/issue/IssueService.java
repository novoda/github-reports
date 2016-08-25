package com.novoda.github.reports.service.issue;

import retrofit2.Response;
import rx.Observable;

import java.util.List;

public interface IssueService {

    Observable<Response<List<GithubIssue>>> getIssuesFor(String organisation,
                                                         String repository,
                                                         GithubIssue.State state,
                                                         String date,
                                                         int page,
                                                         int pageCount);

    Observable<Response<List<GithubEvent>>> getEventsFor(String organisation, String repository, int issueNumber, int page, int pageCount);

    Observable<Response<List<GithubComment>>> getCommentsFor(String organisation,
                                                             String repository,
                                                             int issueNumber,
                                                             String since,
                                                             int page,
                                                             int pageCount);

    Observable<Response<List<GithubReaction>>> getReactionsFor(String organisation,
                                                               String repository,
                                                               int issueNumber,
                                                               int page,
                                                               int pageCount);

}
