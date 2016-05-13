package com.novoda.github.reports.github;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

enum HttpClientContainer {

    INSTANCE;

    private static final String REMAINING_RATE_LIMIT_HEADER = "X-RateLimit-Remaining";

    private final OkHttpClient okHttpClient = buildClient();

    private OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .cache(CacheContainer.INSTANCE.cache())
                .addNetworkInterceptor(chain -> {
                    Request request = injectOAuthTokenThrough(chain);
                    return proceedResponse(chain, request);
                })
                .build();
    }

    private Request injectOAuthTokenThrough(Interceptor.Chain chain) {
        Request oldRequest = chain.request();
        return oldRequest.newBuilder()
                .build();
    }

    private Response proceedResponse(Interceptor.Chain chain, Request request) throws IOException {
        Response response = chain.proceed(request);
        System.out.printf("*** %s requests remaining\n", response.headers().get(REMAINING_RATE_LIMIT_HEADER));
        return response;
    }

    public OkHttpClient okHttpClient() {
        return okHttpClient;
    }

}
