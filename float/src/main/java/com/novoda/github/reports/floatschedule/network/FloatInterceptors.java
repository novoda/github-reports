package com.novoda.github.reports.floatschedule.network;

import com.novoda.github.reports.floatschedule.properties.FloatCredentialsReader;
import com.novoda.github.reports.network.Interceptors;
import com.novoda.github.reports.network.OAuthTokenInterceptor;

public class FloatInterceptors extends Interceptors {

    private static final String FLOAT_AUTH_TOKEN_PREFIX = "Bearer";

    static Interceptors defaultInterceptors() {
        FloatCredentialsReader floatCredentialsReader = FloatCredentialsReader.newInstance();
        String token = floatCredentialsReader.getAuthToken();
        return new FloatInterceptors()
                .withOAuthTokenInterceptor(token);
    }

    FloatInterceptors withOAuthTokenInterceptor(String token) {
        return (FloatInterceptors) with(new OAuthTokenInterceptor(FLOAT_AUTH_TOKEN_PREFIX, token));
    }
}
