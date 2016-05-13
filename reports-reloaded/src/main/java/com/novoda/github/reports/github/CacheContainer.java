package com.novoda.github.reports.github;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import okhttp3.Cache;

enum CacheContainer {

    INSTANCE;

    private static final long MAX_CACHE_SIZE = 4096;

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
        Path local = Paths.get("");
        File cache = new File(local.toFile().getAbsolutePath() + "/buildCache.tmp");
        createNewFile(cache);
        return cache;
    }

    private void createNewFile(File cache) {
        try {
            cache.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Cache cache() {
        return cache;
    }

}
