package com.novoda.github.reports.batch.network;

import com.novoda.github.reports.batch.repository.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import retrofit2.Response;
import rx.Observable;

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

    @Mock
    Observable<Response<List<Repository>>> mockObservable;

    private SystemClock systemClock;

    private RateLimitDelayTransformer rateLimitDelayTransformer;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        systemClock = new TestClock();
        rateLimitDelayTransformer = new RateLimitDelayTransformer(mockRateLimitRemainingCounter, mockRateLimitResetRepository, systemClock);
    }

    @Test
    public void givenRateLimitHasNotBeenExhausted_whenTransforming_thenNoDelayHappens() {
        when(mockRateLimitRemainingCounter.get()).thenReturn(ANY_POSITIVE_REMAINING_RATE_LIMIT);

        Observable actual = rateLimitDelayTransformer.call(mockObservable);

        assertEquals(mockObservable, actual);
    }

    @Test
    public void givenRateLimitHasBeenExhausted_whenTransforming_thenTheSubscriptionIsDelayed() {
        when(mockRateLimitRemainingCounter.get()).thenReturn(0);
        when(mockRateLimitResetRepository.get()).thenReturn(ANY_RESET_TIMESTAMP);

        rateLimitDelayTransformer.call(mockObservable);

        long expectedDelayMillis = ANY_RESET_TIMESTAMP - systemClock.currentTimeMillis();
        verify(mockObservable).delaySubscription(expectedDelayMillis, TimeUnit.MILLISECONDS);
    }

    private static class TestClock implements SystemClock {
        @Override
        public long currentTimeMillis() {
            return 0;
        }
    }
}
