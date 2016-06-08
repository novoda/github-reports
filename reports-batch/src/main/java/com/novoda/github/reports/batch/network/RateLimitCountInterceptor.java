package com.novoda.github.reports.batch.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class RateLimitCountInterceptor implements Interceptor {

    private static final String REMAINING_RATE_LIMIT_HEADER = "X-RateLimit-Remaining";

    private RateLimitRemainingCounter rateLimitRemainingCounter;

    public static RateLimitCountInterceptor newInstance() {
        RateLimitRemainingCounter rateLimitRemainingCounter = RateLimitRemainingCounterContainer.getInstance();
        return new RateLimitCountInterceptor(rateLimitRemainingCounter);
    }

    RateLimitCountInterceptor(RateLimitRemainingCounter rateLimitRemainingCounter) {
        this.rateLimitRemainingCounter = rateLimitRemainingCounter;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        String countAsString = response.headers().get(REMAINING_RATE_LIMIT_HEADER);
        Integer remainingCount = Integer.valueOf(countAsString);
        rateLimitRemainingCounter.set(remainingCount);

        return response;
    }
}
