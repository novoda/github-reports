package com.novoda.github.reports.github.repository;

import com.novoda.github.reports.github.GithubService;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class RepositoryService implements RepoService {

    public void f() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GithubService service = retrofit.create(GithubService.class);

        service.getRepositories("novoda");
    }

}
