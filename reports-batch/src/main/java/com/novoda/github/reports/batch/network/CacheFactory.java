package com.novoda.github.reports.batch.network;

import okhttp3.Cache;

interface CacheFactory {

    Cache createCache();

}
