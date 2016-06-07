package com.novoda.github.reports.batch.retry;

import com.novoda.github.reports.batch.network.RateLimitEncounteredException;
import com.novoda.github.reports.batch.network.RateLimitResetRepository;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import rx.Observable;
import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RetryWhenTokenResetsTest {

    private final static long ANY_TIME_MILLIS = 10000;

    @Mock
    RateLimitResetRepository rateLimitResetRepository;

    @Spy
    Scheduler testScheduler = new TestScheduler();

    @Spy
    RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubject.newInstance(testScheduler);

    @InjectMocks
    RetryWhenTokenResets<Integer> retryWhenTokenResetsTransformer;

    private PublishSubject<Long> timeSubject;
    private TestSubscriber<Integer> testSubscriber;

    @Before
    public void setUp() {
        initMocks(this);
        timeSubject = PublishSubject.create();
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void givenErroringOnceObservableWithRetryMechanism_whenSubscribe_thenResetTimer() {
        Observable<Integer> testObservable = givenErroringOnceObservable();

        whenComposeWithRetryMechanismAndSubscribe(testObservable);

        verify(rateLimitResetTimerSubject).setRateLimitResetTimer(anyLong());
    }

    @Test
    public void givenAlwaysErroringObservableWithRetryMechanism_whenSubscribe_thenNeverComplete() {
        Observable<Integer> testObservable = givenAlwaysErroringObservable();

        whenComposeWithRetryMechanismAndSubscribe(testObservable);

        testSubscriber.assertNoErrors();
        testSubscriber.assertNotCompleted();
    }

    private Observable<Integer> givenAlwaysErroringObservable() {
        return Observable.error(givenRateLimitException());
    }

    @Test
    public void givenErroringOnceObservableWithRetryMechanism_whenSubscribe_thenRetryAndComplete() {
        Observable<Integer> testObservable = givenErroringOnceObservable();

        whenComposeWithRetryMechanismAndSubscribe(testObservable);

        testSubscriber.assertValues(1, 2, 3);
        timeSubject.onNext(ANY_TIME_MILLIS);
        ((TestScheduler) testScheduler).advanceTimeBy(ANY_TIME_MILLIS, TimeUnit.MILLISECONDS);
        testSubscriber.assertValues(1, 2, 3, 1, 2, 3, 4);
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
    }

    private Observable<Integer> givenErroringOnceObservable() {
        final boolean[] errored = {false};
        return Observable.create(subscriber -> {
            subscriber.onNext(1);
            subscriber.onNext(2);
            subscriber.onNext(3);
            if (!errored[0]) {
                subscriber.onError(givenRateLimitException());
                errored[0] = true;
            } else {
                subscriber.onNext(4);
                subscriber.onCompleted();
            }
        });
    }

    private IOException givenRateLimitException() {
        return new IOException(new RateLimitEncounteredException("Banana"));
    }

    private Observable<Integer> whenComposeWithRetryMechanismAndSubscribe(Observable<Integer> observable) {
        observable = observable.compose(retryWhenTokenResetsTransformer);

        when(rateLimitResetRepository.getNextResetTime()).thenReturn(getTimeNow() + ANY_TIME_MILLIS);
        when(rateLimitResetTimerSubject.getTimeObservable()).thenReturn(timeSubject);

        observable.subscribe(testSubscriber);
        return observable;
    }

    private long getTimeNow() {
        return Date.from(Instant.now()).getTime();
    }

}
