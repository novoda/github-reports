package com.novoda.github.reports.service.network;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

class OkHttpClientBuilder {

    private final OkHttpClient.Builder okHttpClientBuilder;
    private Interceptors interceptors;
    private Cache cache;
    private CacheStatsRepository cacheStatsRepository;

    public static OkHttpClientBuilder newInstance() {
        return new OkHttpClientBuilder(new OkHttpClient.Builder());
    }

    OkHttpClientBuilder(OkHttpClient.Builder okHttpClientBuilder) {
        this.okHttpClientBuilder = okHttpClientBuilder;
    }

    OkHttpClientBuilder cache(Cache cache) {
        this.cache = cache;
        return this;
    }

    OkHttpClientBuilder interceptors(Interceptors interceptors) {
        this.interceptors = interceptors;
        return this;
    }

    OkHttpClientBuilder cacheStats(CacheStatsRepository cacheStatsRepository) {
        this.cacheStatsRepository = cacheStatsRepository;
        return this;
    }

    OkHttpClient build() {
        setCache();
        addInterceptors();
        attachCacheToCacheStatsRepository();
        return okHttpClientBuilder.build();
    }

    private void setCache() {
        if (cache != null) {
            okHttpClientBuilder.cache(cache);
        }
    }

    private void addInterceptors() {
        if (interceptors != null) {
            interceptors.stream().forEach(okHttpClientBuilder::addInterceptor);
        }
    }

    private void attachCacheToCacheStatsRepository() {
        if (cacheStatsRepository != null && cache != null) {
            cacheStatsRepository.setCache(cache);
        }
    }

}
