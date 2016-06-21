package com.novoda.github.reports.service.repository;

import com.novoda.github.reports.service.network.GithubApiService;
import com.novoda.github.reports.service.network.GithubCachingServiceContainer;
import com.novoda.github.reports.service.network.GithubServiceContainer;
import com.novoda.github.reports.service.properties.GithubCredentialsReader;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class GithubRepositoryService implements RepositoryService {

    private final GithubApiService githubApiService;

    public static GithubRepositoryService newInstance(GithubCredentialsReader githubCredentialsReader) {
        GithubApiService githubApiService = GithubServiceContainer.getGithubService(githubCredentialsReader);
        return new GithubRepositoryService(githubApiService);
    }

    public static GithubRepositoryService newInstance() {
        GithubApiService githubApiService = GithubServiceContainer.getGithubService();
        return new GithubRepositoryService(githubApiService);
    }

    public static GithubRepositoryService newCachingInstance() {
        GithubApiService githubApiService = GithubCachingServiceContainer.getGithubService();
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
