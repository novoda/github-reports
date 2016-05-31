package com.novoda.github.reports.batch.network;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GithubRateLimitResetRepositoryTest {

    private static final long INITIAL_TIMESTAMP = System.currentTimeMillis();

    private RateLimitResetRepository rateLimitResetRepository;

    @Before
    public void setUp() throws Exception {
        rateLimitResetRepository = new GithubRateLimitResetRepository(INITIAL_TIMESTAMP);
    }

    @Test
    public void givenAnInitialValue_whenWeGetTheCurrentTimestampWithoutSetting_thenItIsTheInitialTimestamp() {

        long actual = rateLimitResetRepository.get();

        assertEquals(actual, INITIAL_TIMESTAMP);
    }

    @Test
    public void whenWeSetATimestamp_thenWeGetThatTimestamp() {
        long expected = INITIAL_TIMESTAMP + 1974;
        rateLimitResetRepository.set(expected);

        assertEquals(expected, rateLimitResetRepository.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenWeSetANegativeTimestamp_thenAnExceptionIsThrown() throws Exception {
        rateLimitResetRepository.set(-INITIAL_TIMESTAMP);
    }

}
