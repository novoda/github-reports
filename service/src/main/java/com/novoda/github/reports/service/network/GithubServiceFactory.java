package com.novoda.github.reports.service.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

class GithubServiceFactory {

    private static final String GITHUB_ENDPOINT = "https://api.github.com/";

    private final OkHttpClient okHttpClient;
    private final GsonConverterFactory gsonConverterFactory;
    private final RxJavaCallAdapterFactory rxJavaCallAdapterFactory;

    public static GithubServiceFactory newInstance() {
        HttpClientFactory httpClientFactory = OkHttpClientFactory.newInstance();
        return newInstance(httpClientFactory);
    }

    public static GithubServiceFactory newCachingInstance() {
        HttpClientFactory httpClientFactory = OkHttpClientFactory.newCachingInstance();
        return newInstance(httpClientFactory);
    }

    private static GithubServiceFactory newInstance(HttpClientFactory httpClientFactory) {
        OkHttpClient okHttpClient = httpClientFactory.createClient();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create();
        RxJavaCallAdapterFactory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();
        return new GithubServiceFactory(okHttpClient, gsonConverterFactory, rxJavaCallAdapterFactory);
    }

    private GithubServiceFactory(OkHttpClient okHttpClient,
                                 GsonConverterFactory gsonConverterFactory,
                                 RxJavaCallAdapterFactory rxJavaCallAdapterFactory) {
        this.okHttpClient = okHttpClient;
        this.gsonConverterFactory = gsonConverterFactory;
        this.rxJavaCallAdapterFactory = rxJavaCallAdapterFactory;
    }

    GithubApiService createService() {
        return createRetrofit()
                .create(GithubApiService.class);
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(GITHUB_ENDPOINT)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();
    }

}
