package com.novoda.github.reports.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cache;

public class CacheStats implements CacheStatsRepository {

    private Cache cache;

    CacheStats() {
        // no op
    }

    @Override
    public int networkCount() {
        throwIfNoCacheSet();
        return cache.networkCount();
    }

    @Override
    public int requestCount() {
        throwIfNoCacheSet();
        return cache.requestCount();
    }

    @Override
    public int hitCount() {
        throwIfNoCacheSet();
        return cache.hitCount();
    }

    @Override
    public int writeSuccessCount() {
        return cache.writeSuccessCount();
    }

    @Override
    public int writeAbortCount() {
        return cache.writeAbortCount();
    }

    @Override
    public List<String> urls() {
        throwIfNoCacheSet();

        List<String> urls = new ArrayList<>();
        try {
            cache.urls().forEachRemaining(urls::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return urls;
    }

    private void throwIfNoCacheSet() {
        if (cache == null) {
            throw new IllegalStateException("No cache is set.");
        }
    }

    @Override
    public void setCache(Cache cache) {
        this.cache = cache;
    }
}
