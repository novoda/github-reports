package com.novoda.github.reports.service.network;

import okhttp3.OkHttpClient;

class OkHttpClientFactory implements HttpClientFactory {

    private final OkHttpClientBuilder okHttpClientBuilder;

    public static OkHttpClientFactory newInstance() {
        OkHttpClientBuilder okHttpClientBuilder = OkHttpClientBuilder.newInstance();
        Interceptors interceptors = Interceptors.defaultInterceptors();

        okHttpClientBuilder
                .withInterceptors(interceptors);

        return new OkHttpClientFactory(okHttpClientBuilder);
    }

    public static OkHttpClientFactory newCachingInstance() {
        OkHttpClientFactory okHttpClientFactory = newInstance();
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