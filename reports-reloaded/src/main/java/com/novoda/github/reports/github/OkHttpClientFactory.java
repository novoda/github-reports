package com.novoda.github.reports.github;

import com.novoda.github.reports.properties.CredentialsReader;

import okhttp3.OkHttpClient;

class OkHttpClientFactory implements HttpClientFactory {

    private final OkHttpClient.Builder okHttpClientBuilder;
    private final CacheFactory cacheFactory;
    private final OAuthTokenInterceptor oAuthTokenInterceptor;
    private final RateLimitCountInterceptor rateLimitCountInterceptor;
    private final RateLimitResetInterceptor rateLimitResetInterceptor;

    static OkHttpClientFactory newInstance() {
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
                oAuthTokenInterceptor,
                rateLimitCountInterceptor,
                rateLimitResetInterceptor
        );
    }

    private OkHttpClientFactory(OkHttpClient.Builder okHttpClientBuilder,
                                CacheFactory cacheFactory,
                                OAuthTokenInterceptor oAuthTokenInterceptor,
                                RateLimitCountInterceptor rateLimitCountInterceptor,
                                RateLimitResetInterceptor rateLimitResetInterceptor) {
        this.okHttpClientBuilder = okHttpClientBuilder;
        this.cacheFactory = cacheFactory;
        this.oAuthTokenInterceptor = oAuthTokenInterceptor;
        this.rateLimitCountInterceptor = rateLimitCountInterceptor;
        this.rateLimitResetInterceptor = rateLimitResetInterceptor;
    }

    @Override
    public OkHttpClient createClient() {
        return okHttpClientBuilder
                .cache(cacheFactory.createCache())
                .addNetworkInterceptor(oAuthTokenInterceptor)
                .addNetworkInterceptor(rateLimitCountInterceptor) // @RUI lambda vs objs (?)
                .addNetworkInterceptor(rateLimitResetInterceptor)
                .build();
    }

}
