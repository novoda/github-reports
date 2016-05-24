package com.novoda.github.reports.github.network;

import com.novoda.github.reports.properties.CredentialsReader;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

class OkHttpClientFactory implements HttpClientFactory {

    private final OkHttpClient.Builder okHttpClientBuilder;
    private final OAuthTokenInterceptor oAuthTokenInterceptor;
    private final RateLimitCountInterceptor rateLimitCountInterceptor;
    private final RateLimitResetInterceptor rateLimitResetInterceptor;

    private final CacheFactory cacheFactory;
    private final CacheStatsRepository cacheStatsRepository;

    static OkHttpClientFactory newInstance(CacheStatsRepository cacheStatsRepository) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        CacheFactory cacheFactory = FileCacheFactory.newInstance();
        CredentialsReader credentialsReader = CredentialsReader.newInstance();
        String token = credentialsReader.getAuthToken();
        OAuthTokenInterceptor oAuthTokenInterceptor = new OAuthTokenInterceptor(token);
        RateLimitCountInterceptor rateLimitCountInterceptor = RateLimitCountInterceptor.newInstance();
        RateLimitResetInterceptor rateLimitResetInterceptor = RateLimitResetInterceptor.newInstance();
        return new OkHttpClientFactory(
                okHttpClientBuilder,
                cacheFactory,
                cacheStatsRepository,
                oAuthTokenInterceptor,
                rateLimitCountInterceptor,
                rateLimitResetInterceptor
        );
    }

    private OkHttpClientFactory(OkHttpClient.Builder okHttpClientBuilder,
                                CacheFactory cacheFactory,
                                CacheStatsRepository cacheStatsRepository,
                                OAuthTokenInterceptor oAuthTokenInterceptor,
                                RateLimitCountInterceptor rateLimitCountInterceptor,
                                RateLimitResetInterceptor rateLimitResetInterceptor) {
        this.okHttpClientBuilder = okHttpClientBuilder;
        this.cacheFactory = cacheFactory;
        this.cacheStatsRepository = cacheStatsRepository;
        this.oAuthTokenInterceptor = oAuthTokenInterceptor;
        this.rateLimitCountInterceptor = rateLimitCountInterceptor;
        this.rateLimitResetInterceptor = rateLimitResetInterceptor;
    }

    @Override
    public OkHttpClient createClient() {
        return okHttpClientBuilder
                .cache(getCache())
                .addNetworkInterceptor(oAuthTokenInterceptor)
                .addNetworkInterceptor(rateLimitCountInterceptor) // @RUI lambda vs objs (?)
                .addNetworkInterceptor(rateLimitResetInterceptor)
                //.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .build();
    }

    private Cache getCache() {
        Cache cache = cacheFactory.createCache();
        cacheStatsRepository.setCache(cache);
        return cache;
    }

}
