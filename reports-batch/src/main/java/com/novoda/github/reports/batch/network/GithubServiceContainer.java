package com.novoda.github.reports.batch.network;

public class GithubServiceContainer {

    private static final GithubApiService githubService = GithubServiceFactory.newInstance().createService();

    private GithubServiceContainer() {
        // non-instantiable
    }

    public static GithubApiService getGithubService() {
        return githubService;
    }
}
