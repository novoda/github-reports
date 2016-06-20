package com.novoda.github.reports.network;

import okhttp3.OkHttpClient;

public class OkHttpClientFactory implements HttpClientFactory {

    private final OkHttpClientBuilder okHttpClientBuilder;

    public static OkHttpClientFactory newInstance(Interceptors interceptors) {
        OkHttpClientBuilder okHttpClientBuilder = OkHttpClientBuilder.newInstance();

        okHttpClientBuilder
                .withInterceptors(interceptors);

        return new OkHttpClientFactory(okHttpClientBuilder);
    }

    public static OkHttpClientFactory newCachingInstance(Interceptors interceptors) {
        OkHttpClientFactory okHttpClientFactory = newInstance(interceptors);
        CacheFactory cacheFactory = FileCacheFactory.newInstance();
        CacheStatsRepository cacheStatsRepository = CacheStatsContainer.getCacheStatsRepository();

        okHttpClientFactory.okHttpClientBuilder
                .withCache(cacheFactory.createCache())
                .withCacheStats(cacheStatsRepository);

        return okHttpClientFactory;
    }

    private OkHttpClientFactory(OkHttpClientBuilder okHttpClientBuilder) {
        this.okHttpClientBuilder = okHttpClientBuilder;
    }

    @Override
    public OkHttpClient createClient() {
        return okHttpClientBuilder.build();
    }

}
