package com.novoda.github.reports.batch.github.network;

import com.novoda.github.reports.batch.properties.GithubCredentialsReader;
import com.novoda.github.reports.batch.properties.PropertiesReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

class OkHttpClientFactory implements HttpClientFactory {

    private static final String GITHUB_PROPERTIES_FILENAME = "github.credentials";

    private final OkHttpClient.Builder okHttpClientBuilder;
    private final List<Interceptor> interceptors = new ArrayList<>();
    private final CacheFactory cacheFactory;
    private final CacheStatsRepository cacheStatsRepository;

    static OkHttpClientFactory newInstance(CacheStatsRepository cacheStatsRepository) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        CacheFactory cacheFactory = FileCacheFactory.newInstance();
        GithubCredentialsReader githubCredentialsReader = GithubCredentialsReader.newInstance(
                PropertiesReader.newInstance(GITHUB_PROPERTIES_FILENAME)
        );
        String token = githubCredentialsReader.getAuthToken();
        Interceptor oAuthTokenInterceptor = new OAuthTokenInterceptor(token);
        Interceptor rateLimitCountInterceptor = RateLimitCountInterceptor.newInstance();
        Interceptor rateLimitResetInterceptor = RateLimitResetInterceptor.newInstance();
        Interceptor customMediaTypeInterceptor = CustomMediaTypeInterceptor.newInstanceForTimelineApi();
        return new OkHttpClientFactory(
                okHttpClientBuilder,
                cacheFactory,
                cacheStatsRepository,
                oAuthTokenInterceptor,
                rateLimitCountInterceptor,
                rateLimitResetInterceptor,
                customMediaTypeInterceptor
        );
    }

    private OkHttpClientFactory(OkHttpClient.Builder okHttpClientBuilder,
                                CacheFactory cacheFactory,
                                CacheStatsRepository cacheStatsRepository,
                                Interceptor... interceptors) {
        this.okHttpClientBuilder = okHttpClientBuilder;
        this.cacheFactory = cacheFactory;
        this.cacheStatsRepository = cacheStatsRepository;
        this.interceptors.addAll(Arrays.asList(interceptors));
    }

    @Override
    public OkHttpClient createClient() {
        interceptors.forEach(okHttpClientBuilder::addInterceptor);
        return okHttpClientBuilder
                .cache(getCache())
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .build();
    }

    private Cache getCache() {
        Cache cache = cacheFactory.createCache();
        cacheStatsRepository.setCache(cache);
        return cache;
    }

}
