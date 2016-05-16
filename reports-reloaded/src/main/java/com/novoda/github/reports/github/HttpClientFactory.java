package com.novoda.github.reports.github;

import com.novoda.github.reports.properties.CredentialsReader;

import okhttp3.OkHttpClient;

public class HttpClientFactory {

    private OkHttpClient.Builder okHttpClientBuilder;
    private CredentialsReader credentialsReader;
    private RateLimitCounter rateLimitCounter;

    public static HttpClientFactory newInstance() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        CredentialsReader credentialsReader = CredentialsReader.newInstance();
        RateLimitCounter rateLimitCounter = RateLimitCounter.newInstance();
        return new HttpClientFactory(okHttpClientBuilder, credentialsReader, rateLimitCounter);
    }

    HttpClientFactory(OkHttpClient.Builder okHttpClientBuilder,
                      CredentialsReader credentialsReader,
                      RateLimitCounter rateLimitCounter) {
        this.okHttpClientBuilder = okHttpClientBuilder;
        this.credentialsReader = credentialsReader;
        this.rateLimitCounter = rateLimitCounter;
    }

    private OkHttpClient buildClient() {
        String token = credentialsReader.getAuthToken();
        return okHttpClientBuilder
                .cache(CacheContainer.INSTANCE.cache()) // TODO extract to CacheFactory
                .addNetworkInterceptor(new OAuthTokenInterceptor(token))
                .addNetworkInterceptor(new RateLimitCountInterceptor(rateLimitCounter)) // @RUI lambda vs objs (?)
                .build();
    }

}
