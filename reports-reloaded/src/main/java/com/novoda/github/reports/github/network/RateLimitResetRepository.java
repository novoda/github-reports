package com.novoda.github.reports.github.network;

interface RateLimitResetRepository {

    long get();

    void set(long timestamp);

}
