package com.novoda.github.reports.service.network;

import com.novoda.github.reports.network.HttpClientFactory;
import com.novoda.github.reports.network.Interceptors;
import com.novoda.github.reports.network.OkHttpClientFactory;

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
        Interceptors githubInterceptors = GithubInterceptors.defaultInterceptors();
        HttpClientFactory httpClientFactory = OkHttpClientFactory.newInstance(githubInterceptors);
        return newInstance(httpClientFactory);
    }

    public static GithubServiceFactory newCachingInstance() {
        Interceptors githubInterceptors = GithubInterceptors.defaultInterceptors();
        HttpClientFactory httpClientFactory = OkHttpClientFactory.newCachingInstance(githubInterceptors);
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
