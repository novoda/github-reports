package com.novoda.github.reports.service.network;

import com.novoda.github.reports.service.properties.GithubCredentialsReader;

public class GithubServiceContainer {

    private static GithubApiService githubService;

    private GithubServiceContainer() {
        // non-instantiable
    }

    public static GithubApiService getGithubService() {
        if (githubService == null) {
            githubService = GithubServiceFactory.newInstance().createService();
        }
        return githubService;
    }

    public static GithubApiService getGithubService(GithubCredentialsReader githubCredentialsReader) {
        if (githubService == null) {
            githubService = GithubServiceFactory.newInstance(githubCredentialsReader).createService();
        }
        return githubService;
    }
}
