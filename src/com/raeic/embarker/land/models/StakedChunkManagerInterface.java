package com.raeic.embarker.land.models;

import com.raeic.embarker.land.enums.StakeCondition;
import com.raeic.embarker.land.enums.UnstakeCondition;
import com.raeic.embarker.utils.LRUCache;

import java.sql.Timestamp;

public interface StakedChunkManagerInterface {
    LRUCache<String, StakedChunk> getCache();

    StakeCondition canStake(String ownerUniqueId, int coordX, int coordZ, String worldName);
    UnstakeCondition canUnstake(String ownerUniqueId, int coordX, int coordZ, String worldName);
    StakedChunk create(int coordX, int coordZ, String worldName, String ownerUniqueId);
    StakedChunk create(int coordX, int coordZ, String worldName, String ownerUniqueId, Timestamp firstStaked, Timestamp lastUpdated);
    StakedChunk findOne(int coordX, int coordZ, String worldName);
}
