package com.novoda.github.reports.service.network;

import okhttp3.Cache;

interface CacheFactory {

    Cache createCache();

}
