package com.novoda.github.reports.github;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public enum GithubServiceContainer {

    INSTANCE;

    private final OkHttpClient okHttpClient = okHttpClient();

    private final GithubService githubService = retrofit()
            .create(GithubService.class);

    private OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                //.addInterceptor()
                //.addNetworkInterceptor()
                .build();
    }

    private Retrofit retrofit() {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public GithubService githubService() {
        return githubService;
    }

}
