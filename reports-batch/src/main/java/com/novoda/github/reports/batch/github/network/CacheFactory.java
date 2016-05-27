package com.novoda.github.reports.batch.github.network;

import okhttp3.Cache;

interface CacheFactory {

    Cache createCache();

}
