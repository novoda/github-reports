package com.novoda.github.reports.github.network;

import com.novoda.github.reports.github.repository.Repository;

import java.util.List;

import okhttp3.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface GithubService {

    @GET("/orgs/{org}/repos")
    Observable<List<Repository>> getRepositoriesFrom(@Path("org") String organisation);

    @GET("/orgs/{org}/repos")
    Observable<Response> getRepositoriesResponsesFrom(@Path("org") String organisation);

}
