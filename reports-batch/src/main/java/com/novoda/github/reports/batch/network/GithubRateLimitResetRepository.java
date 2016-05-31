package com.novoda.github.reports.batch.network;

class GithubRateLimitResetRepository implements RateLimitResetRepository {

    private long timestampMi;

    public static GithubRateLimitResetRepository newInstance() {
        return new GithubRateLimitResetRepository(0);
    }

    GithubRateLimitResetRepository(long timestampSeconds) {
        this.timestamp = timestampSeconds;
    }

    @Override
    public synchronized long get() {
        return timestamp;
    }

    @Override
    public synchronized void set(long timestampSeconds) {
        if (timestampSeconds < 0) {
            throw new IllegalArgumentException("timestamp cannot be negative.");
        }
        this.timestamp = timestampSeconds;
    }
}
