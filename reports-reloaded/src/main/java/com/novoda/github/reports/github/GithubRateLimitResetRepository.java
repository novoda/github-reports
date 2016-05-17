package com.novoda.github.reports.github;

class GithubRateLimitResetRepository implements RateLimitResetRepository{

    private long timestamp;

    GithubRateLimitResetRepository(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public synchronized long get() {
        return timestamp;
    }

    @Override
    public synchronized void set(long timestamp) {
        this.timestamp = timestamp;
    }
}
