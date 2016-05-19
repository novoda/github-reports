package com.novoda.github.reports.github.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class RateLimitResetInterceptor implements Interceptor {

    static final String RATE_LIMIT_RESET_HEADER = "X-RateLimit-Reset";

    private final RateLimitResetRepository rateLimitResetRepository;

    public static RateLimitResetInterceptor newInstance() {
        // FIXME/TODO this repo should be passed in or should be a singleton
        RateLimitResetRepository rateLimitResetRepository = new GithubRateLimitResetRepository(System.currentTimeMillis());
        return new RateLimitResetInterceptor(rateLimitResetRepository);
    }

    RateLimitResetInterceptor(RateLimitResetRepository rateLimitResetRepository) {
        this.rateLimitResetRepository = rateLimitResetRepository;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        String resetTimestampAsString = response.headers().get(RATE_LIMIT_RESET_HEADER);
        rateLimitResetRepository.set(Long.parseLong(resetTimestampAsString));

        return response;
    }
}
