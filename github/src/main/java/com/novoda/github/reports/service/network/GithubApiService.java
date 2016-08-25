package com.novoda.github.reports.service.network;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubEvent;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.issue.GithubReaction;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.service.timeline.TimelineEvent;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import java.util.List;

public interface GithubApiService {

    @GET("/orgs/{org}/repos")
    Observable<Response<List<GithubRepository>>> getRepositoriesResponseForPage(
            @Path("org") String organisation,
            @Query("page") int page,
            @Query("per_page") int perPageCount
    );

    @GET("/repos/{org}/{repo}/issues")
    Observable<Response<List<GithubIssue>>> getIssuesResponseForPage(
            @Path("org") String organisation,
            @Path("repo") String repo,
            @Query("state") GithubIssue.State state,
            @Query("since") String since, // ISO8601: YYYY-MM-DDTHH:MM:SSZ
            @Query("page") int page,
            @Query("per_page") int perPageCount
    );

    @GET("/repos/{org}/{repo}/issues/{issue_number}/events")
    Observable<Response<List<GithubEvent>>> getEventsResponseForIssueAndPage(
            @Path("org") String organisation,
            @Path("repo") String repo,
            @Path("issue_number") int issueNumber,
            @Query("page") int page,
            @Query("per_page") int perPageCount
    );

    @GET("/repos/{org}/{repo}/issues/{issue_number}/comments")
    Observable<Response<List<GithubComment>>> getCommentsResponseForIssueAndPage(
            @Path("org") String organisation,
            @Path("repo") String repo,
            @Path("issue_number") int issueNumber,
            @Query("since") String since, // ISO8601: YYYY-MM-DDTHH:MM:SSZ
            @Query("page") int page,
            @Query("per_page") int perPageCount
    );

    @GET("/repos/{org}/{repo}/pulls/{number}/comments")
    Observable<Response<List<GithubComment>>> getReviewCommentsResponseForPullRequestAndPage(
            @Path("org") String organisation,
            @Path("repo") String repo,
            @Path("number") int pullRequestNumber,
            @Query("since") String since, // ISO8601: YYYY-MM-DDTHH:MM:SSZ
            @Query("page") int page,
            @Query("per_page") int perPageCount
    );

    @GET("/repos/{org}/{repo}/issues/{issue_number}/reactions")
    Observable<Response<List<GithubReaction>>> getReactionsForIssueAndPage(
            @Path("org") String organisation,
            @Path("repo") String repo,
            @Path("issue_number") int issueNumber,
            @Query("page") int page,
            @Query("per_page") int perPageCount
    );

    @GET("/repos/{org}/{repo}/issues/{issue_number}/timeline")
    Observable<Response<List<TimelineEvent>>> getTimelineFor(
            @Path("org") String organisation,
            @Path("repo") String repo,
            @Path("issue_number") int issueNumber,
            @Query("page") int page,
            @Query("per_page") int perPageCount
    );

}
