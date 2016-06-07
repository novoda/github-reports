package com.novoda.github.reports.batch.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class RateLimitResetInterceptor implements Interceptor {

    private static final String RATE_LIMIT_RESET_HEADER = "X-RateLimit-Reset";

    private final RateLimitResetRepository rateLimitResetRepository;
    private final TimeConverter timeConverter;

    public static RateLimitResetInterceptor newInstance() {
        RateLimitResetRepository rateLimitResetRepository = RateLimitRemainingResetRepositoryContainer.getInstance();
        EpochTimeConverter timeConverter = new EpochTimeConverter();
        return new RateLimitResetInterceptor(rateLimitResetRepository, timeConverter);
    }

    RateLimitResetInterceptor(RateLimitResetRepository rateLimitResetRepository, TimeConverter timeConverter) {
        this.rateLimitResetRepository = rateLimitResetRepository;
        this.timeConverter = timeConverter;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        String resetTimestampAsString = response.headers().get(RATE_LIMIT_RESET_HEADER);
        long epochTime = 0;
        try {
            epochTime = Long.parseLong(resetTimestampAsString);
        } catch (NumberFormatException ignored) {
            // ignore this and assume 0
        }
        rateLimitResetRepository.setNextResetTime(timeConverter.toMillis(epochTime));

        return response;
    }
}
