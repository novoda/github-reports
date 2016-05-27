package com.novoda.github.reports.batch.network;

class GithubRateLimitResetRepository implements RateLimitResetRepository {

    private long timestamp;

    public static GithubRateLimitResetRepository newInstance() {
        return new GithubRateLimitResetRepository(System.currentTimeMillis());
    }

    GithubRateLimitResetRepository(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public synchronized long get() {
        return timestamp;
    }

    @Override
    public synchronized void set(long timestamp) {
        if (timestamp < 0) {
            throw new IllegalArgumentException("timestamp cannot be negative.");
        }
        this.timestamp = timestamp;
    }
}
