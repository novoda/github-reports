package com.novoda.github.reports.github.repository;

import com.novoda.github.reports.github.GithubRetrofitService;
import com.novoda.github.reports.github.GithubServiceContainer;

import java.util.List;

import retrofit2.Callback;

public class RepositoryService implements RepoService {

    private GithubRetrofitService githubService;

    public static RepositoryService newInstance() {
        GithubRetrofitService githubService = GithubServiceContainer.INSTANCE.githubService();
        return new RepositoryService(githubService);
    }

    RepositoryService(GithubRetrofitService githubService) {
        this.githubService = githubService;
    }

    public void getRepositoriesOf(String organisation, Callback<List<Repository>> callback) {
        githubService.getRepositories(organisation).enqueue(callback);
    }

}
