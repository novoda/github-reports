package com.novoda.github.reports.batch.network;

public interface RateLimitResetRepository {

    long getNextResetTime();

    void setNextResetTime(long timestamp);

}
