package com.novoda.github.reports.batch.network;

import com.novoda.github.reports.batch.repository.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;
import rx.Observable;

public class RateLimitDelayTransformer implements Observable.Transformer<Response<List<Repository>>, Response<List<Repository>>> {

    private final RateLimitRemainingCounter rateLimitRemainingCounter;
    private final RateLimitResetRepository rateLimitResetRepository;
    private final SystemClock systemClock;

    public static RateLimitDelayTransformer newInstance() {
        RateLimitRemainingCounter rateLimitRemainingCounter = RateLimitRemainingCounterContainer.getInstance();
        RateLimitResetRepository rateLimitResetRepository = RateLimitRemainingResetRepositoryContainer.getInstance();
        SystemClock systemClock = new SystemClock() {};
        return new RateLimitDelayTransformer(rateLimitRemainingCounter, rateLimitResetRepository, systemClock);
    }

    RateLimitDelayTransformer(RateLimitRemainingCounter rateLimitRemainingCounter,
                              RateLimitResetRepository rateLimitResetRepository,
                              SystemClock systemClock) {
        this.rateLimitRemainingCounter = rateLimitRemainingCounter;
        this.rateLimitResetRepository = rateLimitResetRepository;
        this.systemClock = systemClock;
    }

    @Override
    public Observable<Response<List<Repository>>> call(Observable<Response<List<Repository>>> observable) {
        if (hasExhaustedRateLimit()) {
            return observable.delaySubscription(getDelayAmount(), TimeUnit.MILLISECONDS);
        }
        return observable;
    }

    private boolean hasExhaustedRateLimit() {
        return rateLimitRemainingCounter.get() == 0;
    }

    private long getDelayAmount() {
        long resetTimestamp = rateLimitResetRepository.get();
        return resetTimestamp - systemClock.currentTimeMillis();
    }
}
