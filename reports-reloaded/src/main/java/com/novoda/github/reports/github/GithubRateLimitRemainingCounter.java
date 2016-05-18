package com.novoda.github.reports.github;

import java.util.concurrent.atomic.AtomicInteger;

public class GithubRateLimitRemainingCounter implements RateLimitRemainingCounter {

    private static final int INITIAL_VALUE = 5000; // github's limit for auth'ed requests

    private AtomicInteger count;

    static GithubRateLimitRemainingCounter newInstance() {
        return new GithubRateLimitRemainingCounter(INITIAL_VALUE);
    }

    private GithubRateLimitRemainingCounter(int initialValue) {
        count = new AtomicInteger(initialValue);
    }

    @Override
    public int get() {
        return count.get();
    }

    @Override
    public void set(int value) {
        count.set(value);
    }
}
