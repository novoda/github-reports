package com.novoda.github.reports.github;

import com.novoda.github.reports.properties.CredentialsReader;

import okhttp3.OkHttpClient;

class HttpClientFactory {

    private final OkHttpClient.Builder okHttpClientBuilder;
    private final CacheFactory cacheFactory;
    private final CredentialsReader credentialsReader;
    private final RateLimitCounter rateLimitCounter;

    public static HttpClientFactory newInstance() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        CacheFactory cacheFactory = CacheFactory.newInstance();
        CredentialsReader credentialsReader = CredentialsReader.newInstance();
        RateLimitCounter rateLimitCounter = RateLimitCounter.newInstance();
        return new HttpClientFactory(okHttpClientBuilder, cacheFactory, credentialsReader, rateLimitCounter);
    }

    HttpClientFactory(OkHttpClient.Builder okHttpClientBuilder,
                      CacheFactory cacheFactory,
                      CredentialsReader credentialsReader,
                      RateLimitCounter rateLimitCounter) {
        this.okHttpClientBuilder = okHttpClientBuilder;
        this.cacheFactory = cacheFactory;
        this.credentialsReader = credentialsReader;
        this.rateLimitCounter = rateLimitCounter;
    }

    OkHttpClient createClient() {
        String token = credentialsReader.getAuthToken();
        return okHttpClientBuilder
                .cache(cacheFactory.createCache())
                .addNetworkInterceptor(new OAuthTokenInterceptor(token))
                .addNetworkInterceptor(new RateLimitCountInterceptor(rateLimitCounter)) // @RUI lambda vs objs (?)
                .build();
    }

}
