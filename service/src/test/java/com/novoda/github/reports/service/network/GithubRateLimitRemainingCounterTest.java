package com.novoda.github.reports.service.network;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GithubRateLimitRemainingCounterTest {

    private static final int INITIAL_VALUE = 5;

    private RateLimitRemainingCounter rateLimitRemainingCounter;

    @Before
    public void setUp() throws Exception {
        rateLimitRemainingCounter = new GithubRateLimitRemainingCounter(INITIAL_VALUE);
    }

    @Test
    public void givenAnInitialValue_whenWeGetTheCurrentValueWithoutSetting_thenItIsTheInitialValue() {

        int actual = rateLimitRemainingCounter.get();

        assertEquals(actual, INITIAL_VALUE);
    }

    @Test
    public void whenWeSetAValue_thenWeGetThatValue() {
        int expected = 42;
        rateLimitRemainingCounter.set(expected);

        assertEquals(expected, rateLimitRemainingCounter.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenWeSetANegativeValue_thenAnExceptionIsThrown() throws Exception {
        rateLimitRemainingCounter.set(-42);
    }
}
