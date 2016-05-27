package com.novoda.github.reports.batch.github.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GithubServiceFactory implements NetworkServiceFactory {

    private static final String GITHUB_ENDPOINT = "https://api.github.com/";

    private final HttpClientFactory httpClientFactory;
    private final GsonConverterFactory gsonConverterFactory;
    private final RxJavaCallAdapterFactory rxJavaCallAdapterFactory;

    public static GithubServiceFactory newInstance() {
        CacheStatsRepository cacheStatsRepository = CacheStats.INSTANCE;
        HttpClientFactory httpClientFactory = OkHttpClientFactory.newInstance(cacheStatsRepository);
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create();
        RxJavaCallAdapterFactory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();
        return new GithubServiceFactory(httpClientFactory, gsonConverterFactory, rxJavaCallAdapterFactory);
    }

    private GithubServiceFactory(HttpClientFactory httpClientFactory,
                                 GsonConverterFactory gsonConverterFactory,
                                 RxJavaCallAdapterFactory rxJavaCallAdapterFactory) {
        this.httpClientFactory = httpClientFactory;
        this.gsonConverterFactory = gsonConverterFactory;
        this.rxJavaCallAdapterFactory = rxJavaCallAdapterFactory;
    }

    @Override
    public GithubApiService createService() {
        return createRetrofit()
                .create(GithubApiService.class);
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .client(httpClientFactory.createClient())
                .baseUrl(GITHUB_ENDPOINT)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();
    }

}
