package com.novoda.github.reports.service.network;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class RateLimitDelayTransformer<T> implements Observable.Transformer<Response<List<T>>, Response<List<T>>> {

    private final RateLimitRemainingCounter rateLimitRemainingCounter;
    private final RateLimitResetRepository rateLimitResetRepository;
    private final SystemClock systemClock;
    private final Scheduler scheduler;

    public static <T> RateLimitDelayTransformer<T> newInstance() {
        RateLimitRemainingCounter rateLimitRemainingCounter = RateLimitRemainingCounterContainer.getInstance();
        RateLimitResetRepository rateLimitResetRepository = RateLimitRemainingResetRepositoryContainer.getInstance();
        return new RateLimitDelayTransformer<>(rateLimitRemainingCounter, rateLimitResetRepository, new SystemClock() {}, Schedulers.computation());
    }

    RateLimitDelayTransformer(RateLimitRemainingCounter rateLimitRemainingCounter,
                              RateLimitResetRepository rateLimitResetRepository,
                              SystemClock systemClock,
                              Scheduler scheduler) {
        this.rateLimitRemainingCounter = rateLimitRemainingCounter;
        this.rateLimitResetRepository = rateLimitResetRepository;
        this.systemClock = systemClock;
        this.scheduler = scheduler;
    }

    @Override
    public Observable<Response<List<T>>> call(Observable<Response<List<T>>> observable) {
        if (hasExhaustedRateLimit()) {
            return observable.delaySubscription(getDelayAmount(), TimeUnit.MILLISECONDS, scheduler);
        }
        return observable;
    }

    private boolean hasExhaustedRateLimit() {
        return rateLimitRemainingCounter.get() <= 0;
    }

    private long getDelayAmount() {
        long resetTimestamp = rateLimitResetRepository.getNextResetTime();
        return resetTimestamp - systemClock.currentTimeMillis();
    }
}
