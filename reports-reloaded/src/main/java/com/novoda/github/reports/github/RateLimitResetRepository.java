package com.novoda.github.reports.github;

interface RateLimitResetRepository {

    long get();

    void set(long timestamp);

}
