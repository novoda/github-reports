package com.novoda.github.reports.service.network;

public class GithubCachingServiceContainer {

    private static final GithubApiService githubService = GithubServiceFactory.newCachingInstance().createService();

    private GithubCachingServiceContainer() {
        // non-instantiable
    }

    public static GithubApiService getGithubService() {
        return githubService;
    }
}
