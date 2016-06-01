package com.novoda.github.reports.batch.network;

class GithubRateLimitResetRepository implements RateLimitResetRepository {

    private long timestampMillis;

    public static GithubRateLimitResetRepository newInstance() {
        return new GithubRateLimitResetRepository(0);
    }

    GithubRateLimitResetRepository(long timestampMillis) {
        this.timestampMillis = timestampMillis;
    }

    @Override
    public synchronized long getNextResetTime() {
        return timestampMillis;
    }

    @Override
    public synchronized void setNextResetTime(long timestampMillis) {
        if (timestampMillis < 0) {
            throw new IllegalArgumentException("timestampMillis cannot be negative.");
        }
        this.timestampMillis = timestampMillis;
    }
}
