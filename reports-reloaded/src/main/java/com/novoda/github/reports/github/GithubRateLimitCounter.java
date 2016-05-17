package com.novoda.github.reports.github;

import java.util.concurrent.atomic.AtomicInteger;

public class GithubRateLimitCounter implements RateLimitCounter {

    private static final int INITIAL_VALUE = 5000; // github's limit for auth'ed requests

    private AtomicInteger count;

    public static GithubRateLimitCounter newInstance() {
        return new GithubRateLimitCounter(INITIAL_VALUE);
    }

    GithubRateLimitCounter(int initialValue) {
        count = new AtomicInteger(initialValue);
    }

    @Override
    public int get() {
        return count.get();
    }

    @Override
    public int decrement() {
        return count.decrementAndGet();
    }

    @Override
    public void set(int value) {
        count.set(value);
    }
}
