package com.novoda.github.reports.service.network;

import okhttp3.OkHttpClient;

class OkHttpClientFactory implements HttpClientFactory {

    private final OkHttpClientBuilder okHttpClientBuilder;

    public static OkHttpClientFactory newInstance() {
        OkHttpClientBuilder okHttpClientBuilder = OkHttpClientBuilder.newInstance();
        Interceptors interceptors = Interceptors.defaultInterceptors();

        okHttpClientBuilder
                .interceptors(interceptors);

        return new OkHttpClientFactory(okHttpClientBuilder);
    }

    public static OkHttpClientFactory newCachingInstance() {
        OkHttpClientBuilder okHttpClientBuilder = OkHttpClientBuilder.newInstance();
        Interceptors interceptors = Interceptors.defaultInterceptors();
        CacheFactory cacheFactory = FileCacheFactory.newInstance();
        CacheStatsRepository cacheStatsRepository = CacheStatsContainer.getCacheStatsRepository();

        okHttpClientBuilder
                .interceptors(interceptors)
                .cache(cacheFactory.createCache())
                .cacheStats(cacheStatsRepository);

        return new OkHttpClientFactory(okHttpClientBuilder);
    }

    private OkHttpClientFactory(OkHttpClientBuilder okHttpClientBuilder) {
        this.okHttpClientBuilder = okHttpClientBuilder;
    }

    @Override
    public OkHttpClient createClient() {
        return okHttpClientBuilder.build();
    }

}
