package com.novoda.github.reports.batch.network;

interface RateLimitResetRepository {

    long get();

    void set(long timestamp);

}
