package com.novoda.github.reports.github;

import com.novoda.github.reports.github.repository.Repository;

import java.util.List;

import rx.Observable;

public class RepositoriesService implements GithubRepositoryService {

    private GithubService githubService;

    public static RepositoriesService newInstance() {
        GithubService githubService = GithubServiceContainer.INSTANCE.githubService();
        return new RepositoriesService(githubService);
    }

    RepositoriesService(GithubService githubService) {
        this.githubService = githubService;
    }

    @Override
    public Observable<List<Repository>> getRepositoriesFrom(String organisation) {
        return githubService.getRepositoriesFrom(organisation);
    }
}
