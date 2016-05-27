package com.novoda.github.reports.batch.github.network;

interface RateLimitResetRepository {

    long get();

    void set(long timestamp);

}
