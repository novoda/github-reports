package com.novoda.github.reports.service.network;

public class CacheStatsContainer {

    private static final CacheStatsRepository cacheStatsRepository = new CacheStats();

    private CacheStatsContainer() {
        // non-instantiable
    }

    public static CacheStatsRepository getCacheStatsRepository() {
        return cacheStatsRepository;
    }

}
