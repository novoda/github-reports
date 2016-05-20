package com.novoda.github.reports.github.network;

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

}
