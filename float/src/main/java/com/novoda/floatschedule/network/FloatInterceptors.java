package com.novoda.floatschedule.network;

import com.novoda.floatschedule.properties.FloatCredentialsReader;
import com.novoda.github.reports.network.Interceptors;
import com.novoda.github.reports.network.OAuthTokenInterceptor;

public class FloatInterceptors extends Interceptors {

    private static final String FLOAT_AUTH_TOKEN_PREFIX = "Bearer";

    static Interceptors defaultInterceptors() {
        FloatCredentialsReader floatCredentialsReader = FloatCredentialsReader.newInstance();
        String token = floatCredentialsReader.getAuthToken();
        return new FloatInterceptors()
                .withOAuthTokenInterceptor(token)
                .withRateLimitHandlerInterceptor();
    }

    FloatInterceptors withOAuthTokenInterceptor(String token) {
        return (FloatInterceptors) with(new OAuthTokenInterceptor(FLOAT_AUTH_TOKEN_PREFIX, token));
    }

    FloatInterceptors withRateLimitHandlerInterceptor() {
        return (FloatInterceptors) with(new RateLimitHandlerInterceptor());
    }
}
