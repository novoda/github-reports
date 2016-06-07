package com.novoda.github.reports.batch.network;

import com.novoda.github.reports.properties.GithubCredentialsReader;
import com.novoda.github.reports.properties.PropertiesReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

class OkHttpClientFactory implements HttpClientFactory {

    private static final String GITHUB_PROPERTIES_FILENAME = "../github.credentials";

    private final OkHttpClient.Builder okHttpClientBuilder;
    private final List<Interceptor> interceptors = new ArrayList<>();
    private final CacheFactory cacheFactory;
    private final CacheStatsRepository cacheStatsRepository;

    static OkHttpClientFactory newInstance(CacheStatsRepository cacheStatsRepository) {
        return newInstance(cacheStatsRepository, new Interceptor[] {});
    }

    static OkHttpClientFactory newDebugInstance(CacheStatsRepository cacheStatsRepository) {
        Interceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return newInstance(cacheStatsRepository, loggingInterceptor);
    }

    private static OkHttpClientFactory newInstance(CacheStatsRepository cacheStatsRepository, Interceptor... extraInterceptors) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        CacheFactory cacheFactory = FileCacheFactory.newInstance();
        PropertiesReader propertiesReader = PropertiesReader.newInstance(GITHUB_PROPERTIES_FILENAME);
        GithubCredentialsReader githubCredentialsReader = GithubCredentialsReader.newInstance(propertiesReader);
        String token = githubCredentialsReader.getAuthToken();

        Interceptor defaultInterceptors[] = new Interceptor[] {
                new OAuthTokenInterceptor(token),
                RateLimitCountInterceptor.newInstance(),
                RateLimitResetInterceptor.newInstance(),
                CustomMediaTypeInterceptor.newInstanceForTimelineApi()
        };

        return new OkHttpClientFactory(
                okHttpClientBuilder,
                cacheFactory,
                cacheStatsRepository,
                (Interceptor[]) Stream.concat(Arrays.stream(extraInterceptors), Arrays.stream(defaultInterceptors)).toArray()
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
                .build();
    }

    private Cache getCache() {
        Cache cache = cacheFactory.createCache();
        cacheStatsRepository.setCache(cache);
        return cache;
    }

}
