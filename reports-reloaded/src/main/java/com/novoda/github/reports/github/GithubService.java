package com.novoda.github.reports.github;

import com.novoda.github.reports.github.repository.Repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface GithubService {

    @GET("/orgs/{org}/repos")
    Call<List<Repository>> getRepositories(@Path("org") String organisation);

}
