package com.novoda.github.reports.service.network;

import java.util.List;

import okhttp3.Cache;

interface CacheStatsRepository {

    int networkCount();

    int requestCount();

    int hitCount();

    int writeSuccessCount();

    int writeAbortCount();

    List<String> urls();

    void setCache(Cache cache);

    default String describeStats() {
        return String.format(
                "network:%d, request:%d hit:%d, writeSuccess:%d, writeAbort:%d",
                networkCount(),
                requestCount(),
                hitCount(),
                writeSuccessCount(),
                writeAbortCount()
        );
    }
}
