package com.novoda.github.reports.github.repository;

import com.novoda.github.reports.github.network.GithubApiService;
import com.novoda.github.reports.github.network.GithubServiceFactory;

import java.util.List;

import rx.Observable;

class GithubRepositoriesService implements RepositoryService {

    private GithubApiService githubApiService;

    static GithubRepositoriesService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        return new GithubRepositoriesService(githubServiceFactory.createService());
    }

    private GithubRepositoriesService(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    @Override
    public Observable<List<Repository>> getRepositoriesFrom(String organisation) {
        return githubApiService.getRepositoriesFrom(organisation);
    }
}
