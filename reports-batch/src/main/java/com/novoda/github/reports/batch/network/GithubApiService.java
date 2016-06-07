package com.novoda.github.reports.batch.network;

import com.novoda.github.reports.batch.issue.GithubComment;
import com.novoda.github.reports.batch.issue.GithubEvent;
import com.novoda.github.reports.batch.issue.GithubIssue;
import com.novoda.github.reports.batch.repository.GithubRepository;
import com.novoda.github.reports.batch.timeline.TimelineEvent;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface GithubApiService {

    @GET("/orgs/{org}/repos")
    Observable<Response<List<GithubRepository>>> getRepositoriesResponseForPage(
            @Path("org") String organisation,
            @Query("page") Integer page,
            @Query("per_page") Integer perPageCount
    );

    @GET("/repos/{org}/{repo}/issues")
    Observable<Response<List<GithubIssue>>> getIssuesResponseForPage(
            @Path("org") String organisation,
            @Path("repo") String repo,
            @Query("state") GithubIssue.State state,
            @Query("since") String since, // ISO8601: YYYY-MM-DDTHH:MM:SSZ
            @Query("page") Integer page,
            @Query("per_page") Integer perPageCount
    );

    @GET("/repos/{org}/{repo}/issues/{issue_number}/events")
    Observable<Response<List<GithubEvent>>> getEventsResponseForIssueAndPage(
            @Path("org") String organisation,
            @Path("repo") String repo,
            @Path("issue_number") Integer issueNumber,
            @Query("page") Integer page,
            @Query("per_page") Integer perPageCount
    );

    @GET("/repos/{org}/{repo}/issues/{issue_number}/comments")
    Observable<Response<List<GithubComment>>> getCommentsResponseForIssueAndPage(
            @Path("org") String organisation,
            @Path("repo") String repo,
            @Path("issue_number") Integer issueNumber,
            @Query("since") String since, // ISO8601: YYYY-MM-DDTHH:MM:SSZ
            @Query("page") Integer page,
            @Query("per_page") Integer perPageCount
    );

    @GET("/repos/{org}/{repo}/pulls/{number}/comments")
    Observable<Response<List<GithubComment>>> getReviewCommentsResponseForPullRequestAndPage(
            @Path("org") String organisation,
            @Path("repo") String repo,
            @Path("number") Integer pullRequestNumber,
            @Query("since") String since, // ISO8601: YYYY-MM-DDTHH:MM:SSZ
            @Query("page") Integer page,
            @Query("per_page") Integer perPageCount
    );

    @GET("/repos/{org}/{repo}/issues/{issue_number}/timeline")
    Observable<Response<List<TimelineEvent>>> getTimelineFor(
            @Path("org") String organisation,
            @Path("repo") String repo,
            @Path("issue_number") Integer issueNumber,
            @Query("page") Integer page,
            @Query("per_page") Integer perPageCount
    );

}
