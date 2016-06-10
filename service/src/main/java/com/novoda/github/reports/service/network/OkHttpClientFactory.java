package com.novoda.github.reports.service.network;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

class OkHttpClientFactory implements HttpClientFactory {

    private static final String GITHUB_PROPERTIES_FILENAME = "github.credentials";

    private final OkHttpClient.Builder okHttpClientBuilder;
    private final Interceptors interceptors;
    private final CacheFactory cacheFactory;
    private final CacheStatsRepository cacheStatsRepository;

    static OkHttpClientFactory newInstance(CacheStatsRepository cacheStatsRepository) {
        Interceptors interceptors = Interceptors.defaultInterceptors();
        return newInstance(cacheStatsRepository, interceptors);
    }

    static OkHttpClientFactory newDebugInstance(CacheStatsRepository cacheStatsRepository) {
        Interceptors interceptors = Interceptors.defaultInterceptors().withDebugInterceptor();
        return newInstance(cacheStatsRepository, interceptors);
    }

    private static OkHttpClientFactory newInstance(CacheStatsRepository cacheStatsRepository, Interceptors interceptors) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        CacheFactory cacheFactory = FileCacheFactory.newInstance();

        return new OkHttpClientFactory(
                okHttpClientBuilder,
                cacheFactory,
                cacheStatsRepository,
                interceptors
        );
    }

    private OkHttpClientFactory(OkHttpClient.Builder okHttpClientBuilder,
                                CacheFactory cacheFactory,
                                CacheStatsRepository cacheStatsRepository,
                                Interceptors interceptors) {
        this.okHttpClientBuilder = okHttpClientBuilder;
        this.cacheFactory = cacheFactory;
        this.cacheStatsRepository = cacheStatsRepository;
        this.interceptors = interceptors;
    }

    @Override
    public OkHttpClient createClient() {
        interceptors.stream().forEach(okHttpClientBuilder::addInterceptor);
        return okHttpClientBuilder
                .cache(getCache())
                .build();
    }

    private Cache getCache() {
        Cache cache = cacheFactory.createCache();
        cacheStatsRepository.setCache(cache);
        return cache;
    }

}
