package com.novoda.github.reports.batch.network;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import okhttp3.Cache;

class FileCacheFactory implements CacheFactory {

    private static final long MAX_CACHE_SIZE = 10 * 1024 * 1024;
    private static final String BASE_DIR = "";
    private static final String CACHE_DIR = "/.cache/";

    private final File cacheFile;

    static FileCacheFactory newInstance() {
        return new FileCacheFactory(getCacheFile());
    }

    private static File getCacheFile() {
        Path local = Paths.get(BASE_DIR);
        File cache = new File(local.toFile().getAbsolutePath() + CACHE_DIR);
        cache.mkdirs();
        return cache;
    }

    private FileCacheFactory(File cacheFile) {
        this.cacheFile = cacheFile;
    }

    private Cache buildCache() { // TODO could be moved to another thread...
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

    @Override
    public Cache createCache() {
        return buildCache();
    }

}
