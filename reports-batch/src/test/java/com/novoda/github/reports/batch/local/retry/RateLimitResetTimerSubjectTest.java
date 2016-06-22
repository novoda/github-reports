package com.novoda.github.reports.batch.local.retry;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RateLimitResetTimerSubjectTest {

    private static final long ANY_TIME_MILLIS = 1000;
    private static final long[] ANY_DESCENDING_TIME_SERIES_MILLIS = new long[]{1000, 800, 400};

    private RateLimitResetTimerSubject rateLimitResetTimerSubject;
    private Observable<Long> timeSubject;
    private TestScheduler testScheduler;
    private TestSubscriber<Object> testSubscriber;

    @Before
    public void setUp() {
        testScheduler = new TestScheduler();
        rateLimitResetTimerSubject = RateLimitResetTimerSubject.newInstance(testScheduler);
        timeSubject = rateLimitResetTimerSubject.getTimeObservable();
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void givenFreshSubject_whenGetTimer_thenReturnNull() {
        Subscription oldTimer = rateLimitResetTimerSubject.timer;

        assertEquals(null, oldTimer);
    }

    @Test
    public void givenNewResetTime_whenSetRateLimitResetTimer_thenSetNewTimer() {
        rateLimitResetTimerSubject.setRateLimitResetTimer(ANY_TIME_MILLIS);
        Subscription oldTimer = rateLimitResetTimerSubject.timer;

        rateLimitResetTimerSubject.setRateLimitResetTimer(ANY_TIME_MILLIS);
        Subscription newTimer = rateLimitResetTimerSubject.timer;

        assertEquals(true, oldTimer.isUnsubscribed());
        assertNotEquals(oldTimer, newTimer);
    }

    @Test
    public void givenNewResetTime_whenSetRateLimitResetTimer_thenExpectNewValueAfterTimeout() {
        rateLimitResetTimerSubject.setRateLimitResetTimer(ANY_TIME_MILLIS);

        timeSubject.subscribe(testSubscriber);
        testScheduler.advanceTimeBy(ANY_TIME_MILLIS, TimeUnit.MILLISECONDS);

        testSubscriber.assertValue(ANY_TIME_MILLIS);
    }

    @Test
    public void givenMultipleNewResetTimes_whenSetRateLimitResetTimer_thenExpectLatestValueAfterTimeout() {
        rateLimitResetTimerSubject.setRateLimitResetTimer(ANY_DESCENDING_TIME_SERIES_MILLIS[0]);
        rateLimitResetTimerSubject.setRateLimitResetTimer(ANY_DESCENDING_TIME_SERIES_MILLIS[1]);
        rateLimitResetTimerSubject.setRateLimitResetTimer(ANY_DESCENDING_TIME_SERIES_MILLIS[2]);

        timeSubject.subscribe(testSubscriber);
        testScheduler.advanceTimeBy(ANY_DESCENDING_TIME_SERIES_MILLIS[2], TimeUnit.MILLISECONDS);

        testSubscriber.assertValue(ANY_DESCENDING_TIME_SERIES_MILLIS[2]);
    }

    @Test
    public void givenAnyNumberOfResetTimes_whenSetRateLimitResetTimer_thenStreamNeverTerminates() {
        rateLimitResetTimerSubject.setRateLimitResetTimer(ANY_DESCENDING_TIME_SERIES_MILLIS[0]);
        rateLimitResetTimerSubject.setRateLimitResetTimer(ANY_DESCENDING_TIME_SERIES_MILLIS[1]);
        rateLimitResetTimerSubject.setRateLimitResetTimer(ANY_DESCENDING_TIME_SERIES_MILLIS[2]);

        timeSubject.subscribe(testSubscriber);
        testScheduler.advanceTimeBy(ANY_DESCENDING_TIME_SERIES_MILLIS[0], TimeUnit.MILLISECONDS);

        testSubscriber.assertNoTerminalEvent();
    }

}
