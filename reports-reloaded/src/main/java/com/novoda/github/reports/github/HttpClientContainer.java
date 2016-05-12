package com.novoda.github.reports.github;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public enum HttpClientContainer {

    INSTANCE;

    private final OkHttpClient okHttpClient = buildClient();

    private OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(chain -> {
                    Request request = injectOAuthTokenThrough(chain);
                    return proceedResponse(chain, request);
                })
                .build();
    }

    private Request injectOAuthTokenThrough(Interceptor.Chain chain) {
        Request oldRequest = chain.request();
        return oldRequest.newBuilder()
//                .addHeader("", "") // TODO
                .build();
    }

    private Response proceedResponse(Interceptor.Chain chain, Request request) throws IOException {
        Response response = chain.proceed(request);
        response.headers().get("X-RateLimit-Remaining");
        return response;
    }

    public OkHttpClient okHttpClient() {
        return okHttpClient;
    }

}
