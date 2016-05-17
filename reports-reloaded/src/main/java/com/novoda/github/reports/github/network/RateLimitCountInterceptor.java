package com.novoda.github.reports.github.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class RateLimitCountInterceptor implements Interceptor {

    private static final String REMAINING_RATE_LIMIT_HEADER = "X-RateLimit-Remaining";

    private RateLimitRemainingCounter rateLimitRemainingCounter;

    public static RateLimitCountInterceptor newInstance() {
        // FIXME/TODO this counter should be passed in or should be a singleton
        RateLimitRemainingCounter rateLimitRemainingCounter = GithubRateLimitRemainingCounter.newInstance();
        return new RateLimitCountInterceptor(rateLimitRemainingCounter);
    }

    private RateLimitCountInterceptor(RateLimitRemainingCounter rateLimitRemainingCounter) {
        this.rateLimitRemainingCounter = rateLimitRemainingCounter;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        String countAsString = response.headers().get(REMAINING_RATE_LIMIT_HEADER);
        rateLimitRemainingCounter.set(Integer.valueOf(countAsString));

        return response;
    }
}
