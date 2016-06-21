package com.novoda.github.reports.service.network;

import com.novoda.github.reports.service.properties.GithubCredentialsReader;

import okhttp3.OkHttpClient;

class OkHttpClientFactory implements HttpClientFactory {

    private final OkHttpClientBuilder okHttpClientBuilder;

    public static OkHttpClientFactory newInstance(GithubCredentialsReader githubCredentialsReader) {
        Interceptors interceptors = Interceptors.defaultInterceptors(githubCredentialsReader);
        return newInstanceWithInterceptors(interceptors);
    }

    public static OkHttpClientFactory newInstance() {
        Interceptors interceptors = Interceptors.defaultInterceptors();
        return newInstanceWithInterceptors(interceptors);
    }

    private static OkHttpClientFactory newInstanceWithInterceptors(Interceptors interceptors) {
        OkHttpClientBuilder okHttpClientBuilder = OkHttpClientBuilder.newInstance();
        okHttpClientBuilder.withInterceptors(interceptors);
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
