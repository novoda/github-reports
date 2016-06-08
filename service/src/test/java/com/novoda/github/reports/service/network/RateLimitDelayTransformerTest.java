package com.novoda.github.reports.service.network;

import com.novoda.github.reports.service.repository.GithubRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import retrofit2.Response;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

public class RateLimitDelayTransformerTest {

    private static final int ANY_POSITIVE_REMAINING_RATE_LIMIT = 88;
    private static final long ANY_RESET_TIMESTAMP = 23L;

    @Mock
    RateLimitRemainingCounter mockRateLimitRemainingCounter;

    @Mock
    RateLimitResetRepository mockRateLimitResetRepository;

    private TestSubscriber<Response<List<GithubRepository>>> testSubscriber;

    private TestScheduler testScheduler;

    private PublishSubject<Response<List<GithubRepository>>> publishSubject;

    private Observable<Response<List<GithubRepository>>> observable;

    private SystemClock systemClock;

    private RateLimitDelayTransformer<GithubRepository> rateLimitDelayTransformer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        testSubscriber = new TestSubscriber<>();
        testScheduler = new TestScheduler();

        systemClock = new TestClock();

        rateLimitDelayTransformer = new RateLimitDelayTransformer<>(
                mockRateLimitRemainingCounter,
                mockRateLimitResetRepository,
                systemClock,
                testScheduler
        );

        publishSubject = PublishSubject.create();
        observable = publishSubject.asObservable();
    }

    @Test
    public void givenRateLimitHasNotBeenExhausted_whenTransforming_thenNoDelayHappens() {
        Mockito.when(mockRateLimitRemainingCounter.get()).thenReturn(ANY_POSITIVE_REMAINING_RATE_LIMIT);

        Observable actual = rateLimitDelayTransformer.call(observable);

        Assert.assertEquals(observable, actual);
    }

    @Test
    public void givenRateLimitHasBeenExhausted_whenTransforming_thenTheLimitsAreChecked() {
        Mockito.when(mockRateLimitRemainingCounter.get()).thenReturn(0);
        Mockito.when(mockRateLimitResetRepository.getNextResetTime()).thenReturn(ANY_RESET_TIMESTAMP);

        rateLimitDelayTransformer.call(observable);

        Mockito.verify(mockRateLimitRemainingCounter).get();
        Mockito.verify(mockRateLimitResetRepository).getNextResetTime();
    }

    @Test
    public void givenRateLimitHasBeenExhaustedAndDelayGoesBy_whenTransforming_thenGetAnItem() {
        Mockito.when(mockRateLimitRemainingCounter.get()).thenReturn(0);
        Mockito.when(mockRateLimitResetRepository.getNextResetTime()).thenReturn(ANY_RESET_TIMESTAMP);

        Observable<Response<List<GithubRepository>>> delayed = rateLimitDelayTransformer.call(observable);
        delayed.subscribe(testSubscriber);

        long delayMillis = ANY_RESET_TIMESTAMP - systemClock.currentTimeMillis();
        testScheduler.advanceTimeBy(delayMillis, TimeUnit.MILLISECONDS);

        Response<List<GithubRepository>> response = Response.success(new ArrayList<>());
        publishSubject.onNext(response);

        testSubscriber.assertValue(response);
    }

    @Test
    public void givenRateLimitHasBeenExhaustedAndNotEnoughTimeGoesBy_whenTransforming_thenGetAnItem() {
        Mockito.when(mockRateLimitRemainingCounter.get()).thenReturn(0);
        Mockito.when(mockRateLimitResetRepository.getNextResetTime()).thenReturn(ANY_RESET_TIMESTAMP);

        Observable<Response<List<GithubRepository>>> delayed = rateLimitDelayTransformer.call(observable);
        delayed.subscribe(testSubscriber);

        testSubscriber.assertNoValues();
    }

    private static class TestClock implements SystemClock {
        @Override
        public long currentTimeMillis() {
            return 0;
        }
    }
}
