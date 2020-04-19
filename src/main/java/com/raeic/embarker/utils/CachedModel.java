package com.raeic.embarker.utils;

public abstract class CachedModel<T> {
    private static final int CACHE_NUM_OF_OBJECT = 1000000;

    protected LRUCache<String, T> cache;

    public CachedModel() {
        cache = new LRUCache<>(CACHE_NUM_OF_OBJECT);
    }

    public void invalidateCacheByKey(String key) {
        cache.remove(key);
    }
}
