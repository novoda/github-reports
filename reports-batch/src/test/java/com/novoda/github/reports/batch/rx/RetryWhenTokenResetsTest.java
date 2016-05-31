package com.novoda.github.reports.batch.rx;

import com.novoda.github.reports.batch.network.RateLimitResetRepository;

import java.sql.Date;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
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

    @Mock
    RateLimitResetRepository rateLimitResetRepository;

    @Spy
    Scheduler testScheduler = new TestScheduler();

    @Spy
    RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubject.newInstance(testScheduler);

    @InjectMocks
    RetryWhenTokenResets<Integer> retryWhenTokenResetsTransformer;

    private TestSubscriber<Integer> testSubscriber;

    @Before
    public void setUp() {
        initMocks(this);
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void givenErroringOnceObservableWithRetryMechanism_whenSubscribe_thenResetTimer() {
        Observable<Integer> testObservable = givenErroringOnceObservable()
                .compose(retryWhenTokenResetsTransformer);

        when(rateLimitResetRepository.getNextResetTime()).thenReturn(getTimeNow() + 10000);
        when(rateLimitResetTimerSubject.getTimeSubject()).thenReturn(PublishSubject.create());
        testObservable.subscribe(testSubscriber);

        verify(rateLimitResetTimerSubject).setRateLimitResetTimer(anyLong());
    }

    @Test
    public void givenAlwaysErroringObservableWithRetryMechanism_whenSubscribe_thenNeverComplete() {
        Observable<Integer> testObservable = givenAlwaysErroringObservable()
                .compose(retryWhenTokenResetsTransformer);
        PublishSubject<Object> timeSubject = PublishSubject.create();

        long millis = 10000;
        when(rateLimitResetRepository.getNextResetTime()).thenReturn(getTimeNow() + millis);
        when(rateLimitResetTimerSubject.getTimeSubject()).thenReturn(timeSubject);
        testObservable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertNotCompleted();
    }

    private Observable<Integer> givenAlwaysErroringObservable() {
        return Observable.error(givenHttpException());
    }

    @Test
    public void givenErroringOnceObservableWithRetryMechanism_whenSubscribe_thenRetryAndComplete() {
        Observable<Integer> testObservable = givenErroringOnceObservable()
                .compose(retryWhenTokenResetsTransformer);
        PublishSubject<Object> timeSubject = PublishSubject.create();

        long millis = 10000;
        when(rateLimitResetRepository.getNextResetTime()).thenReturn(getTimeNow() + millis);
        when(rateLimitResetTimerSubject.getTimeSubject()).thenReturn(timeSubject);
        testObservable.subscribe(testSubscriber);

        testSubscriber.assertValues(1, 2, 3);
        timeSubject.onNext(millis);
        ((TestScheduler) testScheduler).advanceTimeBy(millis, TimeUnit.MILLISECONDS);
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
                subscriber.onError(givenHttpException());
                errored[0] = true;
            } else {
                subscriber.onNext(4);
                subscriber.onCompleted();
            }
        });
    }

    private HttpException givenHttpException() {
        return new HttpException(Response.error(403, ResponseBody.create(MediaType.parse("application/json"), "{}")));
    }

    private long getTimeNow() {
        return Date.from(Instant.now()).getTime();
    }

}
