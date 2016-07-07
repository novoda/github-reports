package com.novoda.github.reports.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class OAuthTokenInterceptor implements Interceptor {

    private static final String AUTH_TOKEN_HEADER = "Authorization";

    private String tokenPrefix;
    private String oAuthToken;

    public OAuthTokenInterceptor(String tokenPrefix, String oAuthToken) {
        this.tokenPrefix = tokenPrefix;
        this.oAuthToken = oAuthToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = injectOAuthTokenThrough(chain);
        return chain.proceed(request);
    }

    private Request injectOAuthTokenThrough(Chain chain) {
        Request oldRequest = chain.request();
        return oldRequest.newBuilder()
                .addHeader(AUTH_TOKEN_HEADER, tokenPrefix + " " + oAuthToken)
                .build();
    }

}
