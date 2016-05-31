package com.novoda.github.reports.batch.rx;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

class RateLimitResetTimerSubject {

    private PublishSubject<Object> timeSubject;
    Subscription timer;

    public static RateLimitResetTimerSubject newInstance() {
        return new RateLimitResetTimerSubject();
    }

    private RateLimitResetTimerSubject() {
        timeSubject = PublishSubject.create();
    }

    void setRateLimitResetTimer(long millis) {
        setRateLimitResetTimer(millis, Schedulers.computation());
    }

    void setRateLimitResetTimer(long millis, Scheduler scheduler) {
        if (timer != null && !timer.isUnsubscribed()) {
            timer.unsubscribe();
        }
        timer = Observable
                .timer(millis, TimeUnit.MILLISECONDS, scheduler)
                .subscribe(time -> {
                    timeSubject.onNext(millis);
                });
    }

    PublishSubject<Object> getTimeSubject() {
        return timeSubject;
    }

}
