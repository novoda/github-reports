package com.novoda.github.reports.github;

import com.novoda.github.reports.properties.CredentialsReader;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

enum HttpClientContainer {

    INSTANCE;

    private static final String REMAINING_RATE_LIMIT_HEADER = "X-RateLimit-Remaining";
    private static final String AUTH_TOKEN_HEADER = "Authorization";
    public static final String AUTH_TOKEN_PREFIX = "token";

    private final OkHttpClient okHttpClient = buildClient();
    private final String oAuthToken = readAuthToken();

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
                .addHeader(AUTH_TOKEN_HEADER, AUTH_TOKEN_PREFIX + " " + oAuthToken)
                .build();
    }

    private String readAuthToken() {
        CredentialsReader credentialsReader = CredentialsReader.newInstance();
        return credentialsReader.getAuthToken();
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
