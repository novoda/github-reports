package com.novoda.github.reports.service.network;

public interface RateLimitResetRepository {

    long getNextResetTime();

    void setNextResetTime(long timestamp);

}
