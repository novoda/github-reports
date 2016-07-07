package com.novoda.github.reports.network;

import okhttp3.Cache;

interface CacheFactory {

    Cache createCache();

}
