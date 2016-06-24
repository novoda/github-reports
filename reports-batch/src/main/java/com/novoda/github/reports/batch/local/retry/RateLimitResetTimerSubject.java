package com.novoda.github.reports.batch.local.retry;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class RateLimitResetTimerSubject {

    private final Scheduler scheduler;
    private final PublishSubject<Long> timeSubject;
    Subscription timer;

    public static RateLimitResetTimerSubject newInstance(Scheduler scheduler) {
        return new RateLimitResetTimerSubject(scheduler);
    }

    public static RateLimitResetTimerSubject newInstance() {
        return new RateLimitResetTimerSubject(Schedulers.computation());
    }

    private RateLimitResetTimerSubject(Scheduler scheduler) {
        timeSubject = PublishSubject.create();
        this.scheduler = scheduler;
    }

    void setRateLimitResetTimer(long millis) {
        if (timer != null && !timer.isUnsubscribed()) {
            timer.unsubscribe();
        }
        timer = Observable
                .timer(millis, TimeUnit.MILLISECONDS, scheduler)
                .subscribe(time -> {
                    timeSubject.onNext(millis);
                });
    }

    Observable<Long> getTimeObservable() {
        return timeSubject.asObservable();
    }

}
