package com.novoda.github.reports.batch.network;

class GithubRateLimitResetRepository implements RateLimitResetRepository {

    private long timestamp;

    GithubRateLimitResetRepository(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public synchronized long getNextResetTime() {
        return timestamp;
    }

    @Override
    public synchronized void setNextResetTime(long timestamp) {
        if (timestamp < 0) {
            throw new IllegalArgumentException("timestamp cannot be negative.");
        }
        this.timestamp = timestamp;
    }
}
