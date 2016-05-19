package com.novoda.github.reports.github.network;

public interface CacheStatsRepository {

    int networkCount();

    int requestCount();

    Iterable<String> urls();

}
