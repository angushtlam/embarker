package com.raeic.embarker.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private int cacheSize;

    public LRUCache(int cacheSize) {
        super(16, 0.75F, true);
        this.cacheSize = cacheSize;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        V value = eldest.getValue();
        if (value instanceof ModelClass) {
            ModelClass modelClass = ((ModelClass) value);

            // Delete it from the database if the model is deleted, otherwise save it
            if (modelClass.isDeleted()) {
                modelClass.delete();
            } else {
                modelClass.save();
            }
        }

        return size() >= cacheSize;
    }
}
