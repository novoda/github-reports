package com.novoda.github.reports.service.repository;

import com.novoda.github.reports.service.network.GithubApiService;
import com.novoda.github.reports.service.network.GithubServiceContainer;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class GithubRepositoriesService implements RepositoryService {

    private final GithubApiService githubApiService;

    public static GithubRepositoriesService newInstance() {
        GithubApiService githubApiService = GithubServiceContainer.getGithubService();
        return new GithubRepositoriesService(githubApiService);
    }

    private GithubRepositoriesService(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    @Override
    public Observable<Response<List<GithubRepository>>> getRepositoriesFor(String organisation, int page, int pageCount) {
        return githubApiService.getRepositoriesResponseForPage(organisation, page, pageCount);
    }

}
