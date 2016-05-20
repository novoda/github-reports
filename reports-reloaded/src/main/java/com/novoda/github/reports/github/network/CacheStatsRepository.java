package com.novoda.github.reports.github.network;

import java.util.List;

import okhttp3.Cache;

public interface CacheStatsRepository {

    int networkCount();

    int requestCount();

    int hitCount();

    List<String> urls();

    void setCache(Cache cache);

}
