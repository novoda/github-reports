package com.novoda.github.reports.github.network;

import okhttp3.Cache;

interface CacheFactory {

    Cache createCache();

}
