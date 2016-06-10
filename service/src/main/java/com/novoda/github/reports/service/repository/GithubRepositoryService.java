package com.novoda.github.reports.service.repository;

import com.novoda.github.reports.service.network.GithubApiService;
import com.novoda.github.reports.service.network.GithubServiceContainer;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class GithubRepositoryService implements RepositoryService {

    private final GithubApiService githubApiService;

    public static GithubRepositoryService newInstance() {
        GithubApiService githubApiService = GithubServiceContainer.getGithubService();
        return new GithubRepositoryService(githubApiService);
    }

    private GithubRepositoryService(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    @Override
    public Observable<Response<List<GithubRepository>>> getRepositoriesFor(String organisation, int page, int pageCount) {
        return githubApiService.getRepositoriesResponseForPage(organisation, page, pageCount);
    }

}
