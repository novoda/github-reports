package com.novoda.github.reports.github;

import com.novoda.github.reports.properties.CredentialsReader;

import okhttp3.OkHttpClient;

class OkHttpClientFactory implements HttpClientFactory {

    private final OkHttpClient.Builder okHttpClientBuilder;
    private final CacheFactory cacheFactory;
    private final CredentialsReader credentialsReader;
    private final RateLimitRemainingCounter rateLimitRemainingCounter;

    static OkHttpClientFactory newInstance() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        CacheFactory cacheFactory = FileCacheFactory.newInstance();
        CredentialsReader credentialsReader = CredentialsReader.newInstance();
        RateLimitRemainingCounter rateLimitRemainingCounter = GithubRateLimitRemainingCounter.newInstance();
        return new OkHttpClientFactory(okHttpClientBuilder, cacheFactory, credentialsReader, rateLimitRemainingCounter);
    }

    private OkHttpClientFactory(OkHttpClient.Builder okHttpClientBuilder,
                                CacheFactory cacheFactory,
                                CredentialsReader credentialsReader,
                                RateLimitRemainingCounter rateLimitRemainingCounter) {
        this.okHttpClientBuilder = okHttpClientBuilder;
        this.cacheFactory = cacheFactory;
        this.credentialsReader = credentialsReader;
        this.rateLimitRemainingCounter = rateLimitRemainingCounter;
    }

    @Override
    public OkHttpClient createClient() {
        String token = credentialsReader.getAuthToken();
        return okHttpClientBuilder
                .cache(cacheFactory.createCache())
                .addNetworkInterceptor(new OAuthTokenInterceptor(token))
                .addNetworkInterceptor(new RateLimitCountInterceptor(rateLimitRemainingCounter)) // @RUI lambda vs objs (?)
                .build();
    }

}
