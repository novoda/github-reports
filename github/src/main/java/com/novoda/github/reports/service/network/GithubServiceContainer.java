package com.novoda.github.reports.service.network;

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

}
