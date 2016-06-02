package com.novoda.github.reports.batch.network;

import com.novoda.github.reports.batch.repository.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class RepositoryRateLimitDelayTransformer implements Observable.Transformer<Response<List<Repository>>, Response<List<Repository>>> {

    private final RateLimitRemainingCounter rateLimitRemainingCounter;
    private final RateLimitResetRepository rateLimitResetRepository;
    private final SystemClock systemClock;
    private final Scheduler scheduler;

    public static RepositoryRateLimitDelayTransformer newInstance() {
        RateLimitRemainingCounter rateLimitRemainingCounter = RateLimitRemainingCounterContainer.getInstance();
        RateLimitResetRepository rateLimitResetRepository = RateLimitRemainingResetRepositoryContainer.getInstance();
        return new RepositoryRateLimitDelayTransformer(rateLimitRemainingCounter, rateLimitResetRepository, new SystemClock() {}, Schedulers.computation());
    }

    RepositoryRateLimitDelayTransformer(RateLimitRemainingCounter rateLimitRemainingCounter,
                                        RateLimitResetRepository rateLimitResetRepository,
                                        SystemClock systemClock,
                                        Scheduler scheduler) {
        this.rateLimitRemainingCounter = rateLimitRemainingCounter;
        this.rateLimitResetRepository = rateLimitResetRepository;
        this.systemClock = systemClock;
        this.scheduler = scheduler;
    }

    @Override
    public Observable<Response<List<Repository>>> call(Observable<Response<List<Repository>>> observable) {
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
