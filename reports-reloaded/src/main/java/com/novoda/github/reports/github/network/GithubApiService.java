package com.novoda.github.reports.github.network;

import com.novoda.github.reports.github.State;
import com.novoda.github.reports.github.issue.Issue;
import com.novoda.github.reports.github.repository.Repository;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface GithubApiService {

    @GET("/orgs/{org}/repos")
    Observable<Response<List<Repository>>> getRepositoriesResponseForPage(@Path("org") String organisation, @Query("page") Integer page);

    @GET("/orgs/{org}/repos")
    Observable<Response<List<Repository>>> getRepositoriesResponseForPage(
            @Path("org") String organisation,
            @Query("page") Integer page,
            @Query("per_page") Integer perPageCount
    );

    @GET("/repos/{org}/{repo}/issues")
    Observable<Response<List<Issue>>> getIssuesResponseForPage(
            @Path("org") String organisation,
            @Path("repo") String repo,
            @Query("state") State state,
            @Query("since") String since, // ISO8601: YYYY-MM-DDTHH:MM:SSZ
            @Query("page") Integer page,
            @Query("per_page") Integer perPageCount
    );

}
