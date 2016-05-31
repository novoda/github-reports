package com.novoda.github.reports.batch.rx;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RateLimitResetTimerSubjectTest {

    private RateLimitResetTimerSubject rateLimitResetTimerSubject;
    private PublishSubject<Long> timeSubject;
    private TestScheduler testScheduler;
    private TestSubscriber<Object> testSubscriber;

    @Before
    public void setUp() {
        testScheduler = new TestScheduler();
        rateLimitResetTimerSubject = RateLimitResetTimerSubject.newInstance(testScheduler);
        timeSubject = rateLimitResetTimerSubject.getTimeSubject();
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void givenFreshSubject_whenGetTimer_thenReturnNull() {
        Subscription oldTimer = rateLimitResetTimerSubject.timer;

        assertEquals(null, oldTimer);
    }

    @Test
    public void givenNewResetTime_whenSetRateLimitResetTimer_thenSetNewTimer() {
        Subscription oldTimer = rateLimitResetTimerSubject.timer;

        rateLimitResetTimerSubject.setRateLimitResetTimer(1000);
        Subscription newTimer = rateLimitResetTimerSubject.timer;

        assertNotEquals(oldTimer, newTimer);
    }

    @Test
    public void givenNewResetTime_whenSetRateLimitResetTimer_thenExpectNewValueAfterTimeout() {
        long millis = 1000;

        rateLimitResetTimerSubject.setRateLimitResetTimer(millis);

        timeSubject.subscribe(testSubscriber);
        testScheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);

        testSubscriber.assertValue(millis);
    }

    @Test
    public void givenMultipleNewResetTimes_whenSetRateLimitResetTimer_thenExpectLatestValueAfterTimeout() {
        long[] millis = new long[] {1000, 800, 400};

        rateLimitResetTimerSubject.setRateLimitResetTimer(millis[0]);
        rateLimitResetTimerSubject.setRateLimitResetTimer(millis[1]);
        rateLimitResetTimerSubject.setRateLimitResetTimer(millis[2]);

        timeSubject.subscribe(testSubscriber);
        testScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS);

        testSubscriber.assertValue(millis[2]);
    }

    @Test
    public void givenAnyNumberOfResetTimes_whenSetRateLimitResetTimer_thenStreamNeverTerminates() {
        long[] millis = new long[]{1000, 800, 400};

        rateLimitResetTimerSubject.setRateLimitResetTimer(millis[0]);
        rateLimitResetTimerSubject.setRateLimitResetTimer(millis[1]);
        rateLimitResetTimerSubject.setRateLimitResetTimer(millis[2]);

        timeSubject.subscribe(testSubscriber);
        testScheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);

        testSubscriber.assertNoTerminalEvent();
    }

}
