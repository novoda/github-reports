package com.novoda.github.reports.github.network;

import java.util.concurrent.atomic.AtomicInteger;

class GithubRateLimitRemainingCounter implements RateLimitRemainingCounter {

    private static final int INITIAL_VALUE = 5000; // github's limit for auth'ed requests

    private AtomicInteger count;

    static GithubRateLimitRemainingCounter newInstance() {
        return new GithubRateLimitRemainingCounter(INITIAL_VALUE);
    }

    GithubRateLimitRemainingCounter(int initialValue) {
        count = new AtomicInteger(initialValue);
    }

    @Override
    public int get() {
        return count.get();
    }

    @Override
    public void set(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("value cannot be negative.");
        }
        count.set(value);
    }
}
