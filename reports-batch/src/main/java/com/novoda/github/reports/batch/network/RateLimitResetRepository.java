package com.novoda.github.reports.batch.network;

public interface RateLimitResetRepository {

    long get();

    void set(long timestampSeconds);

}
