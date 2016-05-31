package com.novoda.github.reports.batch.rx;

import com.novoda.github.reports.batch.network.RateLimitResetRepository;

import java.time.Instant;
import java.util.Date;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;

class RetryWhenTokenResets<T> implements Observable.Transformer<T, T> {

    private final RateLimitResetRepository rateLimitResetRepository;
    private final RateLimitResetTimerSubject resetTimerSubject;

    public static <T> RetryWhenTokenResets<T> newInstance(
            RateLimitResetTimerSubject rateLimitResetTimerSubject,
            RateLimitResetRepository rateLimitResetRepository) {
        return new RetryWhenTokenResets<>(rateLimitResetTimerSubject, rateLimitResetRepository);
    }

    private RetryWhenTokenResets(
            RateLimitResetTimerSubject rateLimitResetTimerSubject,
            RateLimitResetRepository rateLimitResetRepository) {
        this.resetTimerSubject = rateLimitResetTimerSubject;
        this.rateLimitResetRepository = rateLimitResetRepository;
    }

    @Override
    public Observable<T> call(Observable<T> inObservable) {
        return inObservable.retryWhen(errors -> errors.switchMap(error -> {
            if (error instanceof HttpException) {
                long nextTick = getTimeDiffInMillisFromNow(rateLimitResetRepository.getNextResetTime());
                resetTimerSubject.setRateLimitResetTimer(nextTick);
                return resetTimerSubject.getTimeSubject().take(1);
            }
            return Observable.<Long>error(error);
        }));
    }

    private long getTimeDiffInMillisFromNow(long timestamp) {
        Date now = Date.from(Instant.now());
        long nowMillis = now.getTime();
        return timestamp - nowMillis;
    }

}
