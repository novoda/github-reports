package com.novoda.github.reports.github;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

enum GithubServiceContainer {

    INSTANCE;

    private static final String GITHUB_ENDPOINT = "https://api.github.com/";

    private final OkHttpClient okHttpClient = HttpClientContainer.INSTANCE.okHttpClient();
    private final GithubService githubService = retrofit().create(GithubService.class);

    private Retrofit retrofit() {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(GITHUB_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    GithubService githubService() {
        return githubService;
    }

}
