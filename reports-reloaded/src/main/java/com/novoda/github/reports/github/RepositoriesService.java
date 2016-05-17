package com.novoda.github.reports.github;

import com.novoda.github.reports.github.repository.Repository;

import java.util.List;

import rx.Observable;

class RepositoriesService implements GithubRepositoryService {

    private GithubService githubService;

    static RepositoriesService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        return new RepositoriesService(githubServiceFactory.createService());
    }

    private RepositoriesService(GithubService githubService) {
        this.githubService = githubService;
    }

    @Override
    public Observable<List<Repository>> getRepositoriesFrom(String organisation) {
        return githubService.getRepositoriesFrom(organisation);
    }
}
