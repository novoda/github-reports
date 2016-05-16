package com.novoda.github.reports.github;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

class GithubServiceFactory {

    private static final String GITHUB_ENDPOINT = "https://api.github.com/";

    private final HttpClientFactory httpClientFactory;
    private final GsonConverterFactory gsonConverterFactory;
    private final RxJavaCallAdapterFactory rxJavaCallAdapterFactory;

    public static GithubServiceFactory newInstance() {
        HttpClientFactory httpClientFactory = HttpClientFactory.newInstance();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create();
        RxJavaCallAdapterFactory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();
        return new GithubServiceFactory(httpClientFactory, gsonConverterFactory, rxJavaCallAdapterFactory);
    }

    GithubServiceFactory(HttpClientFactory httpClientFactory,
                         GsonConverterFactory gsonConverterFactory,
                         RxJavaCallAdapterFactory rxJavaCallAdapterFactory) {
        this.httpClientFactory = httpClientFactory;
        this.gsonConverterFactory = gsonConverterFactory;
        this.rxJavaCallAdapterFactory = rxJavaCallAdapterFactory;
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .client(httpClientFactory.createClient())
                .baseUrl(GITHUB_ENDPOINT)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();
    }

    public GithubService createService() {
        return createRetrofit()
                .create(GithubService.class);
    }

}
