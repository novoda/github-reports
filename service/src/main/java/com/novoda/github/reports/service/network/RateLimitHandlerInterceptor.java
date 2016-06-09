package com.novoda.github.reports.service.network;

import java.io.IOException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class RateLimitHandlerInterceptor implements Interceptor {

    private static final int HTTP_STATUS_CODE_UNAUTHORIZED = 403;

    private final RateLimitRemainingCounter rateLimitRemainingCounter;
    private final RateLimitResetRepository rateLimitResetRepository;

    public static RateLimitHandlerInterceptor newInstance() {
        RateLimitRemainingCounter rateLimitRemainingCounter = RateLimitRemainingCounterContainer.getInstance();
        RateLimitResetRepository rateLimitResetRepository = RateLimitRemainingResetRepositoryContainer.getInstance();
        return new RateLimitHandlerInterceptor(rateLimitRemainingCounter, rateLimitResetRepository);
    }

    private RateLimitHandlerInterceptor(RateLimitRemainingCounter rateLimitRemainingCounter, RateLimitResetRepository rateLimitResetRepository) {
        this.rateLimitRemainingCounter = rateLimitRemainingCounter;
        this.rateLimitResetRepository = rateLimitResetRepository;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        int remainingCount = rateLimitRemainingCounter.get();

        if (response.code() == HTTP_STATUS_CODE_UNAUTHORIZED && remainingCount <= 0) {
            long resetTime = rateLimitResetRepository.getNextResetTime();
            Date resetDate = new Date(resetTime);
            throw new IOException(new RateLimitEncounteredException("Rate limit encountered, retry at " + resetDate.toString(), resetDate));
        }

        return response;
    }
}
