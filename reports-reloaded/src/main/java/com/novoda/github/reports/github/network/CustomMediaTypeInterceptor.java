package com.novoda.github.reports.github.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CustomMediaTypeInterceptor implements Interceptor {

    private static final String ACCEPT_HEADER_KEY = "Accept";
    private static final String AUTH_TOKEN_PREFIX = "token";

    private final String customMediaType;

    CustomMediaTypeInterceptor(String type) {
        //
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = injectOAuthTokenThrough(chain);
        return chain.proceed(request);
    }

    private Request injectOAuthTokenThrough(Chain chain) {
        Request oldRequest = chain.request();
        return oldRequest.newBuilder()
                .addHeader(AUTH_TOKEN_HEADER, AUTH_TOKEN_PREFIX + " " + oAuthToken)
                .build();
    }
}
