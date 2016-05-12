package com.novoda.github.reports.github.repository;

import com.novoda.github.reports.github.GithubService;
import com.novoda.github.reports.github.GithubServiceContainer;

import java.util.List;

import retrofit2.Callback;

public class RepositoryService implements RepoService {

    private GithubService githubService;

    public static RepositoryService newInstance() {
        GithubService githubService = GithubServiceContainer.INSTANCE.githubService();
        return new RepositoryService(githubService);
    }

    RepositoryService(GithubService githubService) {
        this.githubService = githubService;
    }

    public void getRepositoriesOf(String organisation, Callback<List<Repository>> callback) {
        githubService.getRepositories(organisation).enqueue(callback);
    }

}
