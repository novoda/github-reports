package com.novoda.github.reports.github.repository;

import com.novoda.github.reports.github.network.GithubService;
import com.novoda.github.reports.github.network.GithubServiceFactory;

import java.util.List;

import rx.Observable;

class GithubRepositoriesService implements RepositoryService {

    private GithubService githubService;

    static GithubRepositoriesService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        return new GithubRepositoriesService(githubServiceFactory.createService());
    }

    private GithubRepositoriesService(GithubService githubService) {
        this.githubService = githubService;
    }

    @Override
    public Observable<List<Repository>> getRepositoriesFrom(String organisation) {
        return githubService.getRepositoriesFrom(organisation);
    }
}
