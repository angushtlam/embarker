package com.raeic.embarker.player.models;

public interface EmbarkerPlayerManagerInterface {
    void invalidateCacheByKey(String uniqueId);
    EmbarkerPlayer findOne(String uniqueId);
}
