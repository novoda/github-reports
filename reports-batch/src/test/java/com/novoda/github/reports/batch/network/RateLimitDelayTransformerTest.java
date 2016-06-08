package com.novoda.github.reports.batch.network;

import com.novoda.github.reports.batch.repository.GithubRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import retrofit2.Response;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
        initMocks(this);

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
        when(mockRateLimitRemainingCounter.get()).thenReturn(ANY_POSITIVE_REMAINING_RATE_LIMIT);

        Observable actual = rateLimitDelayTransformer.call(observable);

        assertEquals(observable, actual);
    }

    @Test
    public void givenRateLimitHasBeenExhausted_whenTransforming_thenTheLimitsAreChecked() {
        when(mockRateLimitRemainingCounter.get()).thenReturn(0);
        when(mockRateLimitResetRepository.getNextResetTime()).thenReturn(ANY_RESET_TIMESTAMP);

        rateLimitDelayTransformer.call(observable);

        verify(mockRateLimitRemainingCounter).get();
        verify(mockRateLimitResetRepository).getNextResetTime();
    }

    @Test
    public void givenRateLimitHasBeenExhaustedAndDelayGoesBy_whenTransforming_thenGetAnItem() {
        when(mockRateLimitRemainingCounter.get()).thenReturn(0);
        when(mockRateLimitResetRepository.getNextResetTime()).thenReturn(ANY_RESET_TIMESTAMP);

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
        when(mockRateLimitRemainingCounter.get()).thenReturn(0);
        when(mockRateLimitResetRepository.getNextResetTime()).thenReturn(ANY_RESET_TIMESTAMP);

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
