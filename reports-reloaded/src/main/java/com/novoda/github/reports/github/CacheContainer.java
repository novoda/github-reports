package com.novoda.github.reports.github;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import okhttp3.Cache;

enum CacheContainer {

    INSTANCE;

    private static final long MAX_CACHE_SIZE = 4096;
    private static final String BASE_DIR = "";
    private static final String CACHE_DIR = "/.cache/";

    private Cache cache = buildCache();

    private Cache buildCache() { // TODO could be moved to another thread...
        File cacheFile = cacheFile();
        Cache cache = new Cache(cacheFile, MAX_CACHE_SIZE);
        initializeCache(cache);
        return cache;
    }

    private void initializeCache(Cache cache) {
        try {
            cache.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File cacheFile() {
        Path local = Paths.get(BASE_DIR);
        File cache = new File(local.toFile().getAbsolutePath() + CACHE_DIR);
        cache.mkdirs();
        return cache;
    }

    Cache cache() {
        return cache;
    }

}
