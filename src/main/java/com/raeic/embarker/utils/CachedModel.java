package com.raeic.embarker.utils;

import com.raeic.embarker.land.models.StakedChunk;

public abstract class CachedModel {
    private static final int CACHE_NUM_OF_OBJECT = 1000000;

    protected LRUCache<String, StakedChunk> cache;

    public CachedModel() {
        cache = new LRUCache<>(CACHE_NUM_OF_OBJECT);
    }

    public void invalidateCacheByKey(String key) {
        cache.remove(key);
    }
}
