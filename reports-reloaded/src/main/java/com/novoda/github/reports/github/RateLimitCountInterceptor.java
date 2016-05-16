package com.novoda.github.reports.github;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class RateLimitCountInterceptor implements Interceptor {

    private static final String REMAINING_RATE_LIMIT_HEADER = "X-RateLimit-Remaining";

    private RateLimitCounter rateLimitCounter;

    RateLimitCountInterceptor(RateLimitCounter rateLimitCounter) {
        this.rateLimitCounter = rateLimitCounter;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        response.headers().get(REMAINING_RATE_LIMIT_HEADER); // TODO rateLimitCounter

        return response;
    }
}
