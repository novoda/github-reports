package com.novoda.github.reports.github;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public enum GithubServiceContainer {

    INSTANCE;

    private static final String GITHUB_ENDPOINT = "https://api.github.com/";

    private final OkHttpClient okHttpClient = HttpClientContainer.INSTANCE.okHttpClient();
    private final GithubService githubService = retrofit().create(GithubService.class);

    private Retrofit retrofit() {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(GITHUB_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public GithubService githubService() {
        return githubService;
    }

}
