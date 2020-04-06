package com.raeic.embarker.player.models;

import com.raeic.embarker.Globals;
import com.raeic.embarker.db.DB;
import com.raeic.embarker.land.models.StakedChunk;
import com.raeic.embarker.utils.LRUCache;

import java.sql.*;
import java.util.HashSet;

public class EmbarkerPlayerManager implements EmbarkerPlayerManagerInterface {
    private static final int CACHE_NUM_OF_OBJECT = 1000000;

    private LRUCache<String, EmbarkerPlayer> cache;

    public EmbarkerPlayerManager() {
        cache = new LRUCache<>(CACHE_NUM_OF_OBJECT);
    }

    @Override
    public void invalidateCacheByKey(String uniqueId) {
        cache.remove(uniqueId);
    }

    @Override
    public EmbarkerPlayer findOne(String uniqueId) {
        // Check if the cache contains the object. If it does, just return it from the cache.
        if (cache.containsKey(uniqueId)) {
            return cache.get(uniqueId);
        }
        
        String sql = "select " +
                     "  coordX, coordZ, worldName, firstStaked, lastUpdated " +
                     "from embarkerstakedchunk " +
                     "where ownerUniqueId = '" + uniqueId + "'";

        EmbarkerPlayer result = null;

        try {
            Connection conn = DB.getConnection();
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);

            if (results != null) {
                HashSet<StakedChunk> stakedChunks = new HashSet<>();

                while (results.next()) {
                    int coordX = results.getInt("coordX");
                    int coordZ = results.getInt("coordZ");
                    String worldName = results.getString("worldName");
                    Timestamp firstStaked = results.getTimestamp("firstStaked");
                    Timestamp lastUpdated = results.getTimestamp("lastUpdated");

                    // Add the cached chunk if it's there, otherwise make a new one.
                    StakedChunk newStakedChunk = Globals.stakedChunks.findOne(coordX, coordZ, worldName);
                    if (newStakedChunk != null) {
                        stakedChunks.add(newStakedChunk);
                    } else {
                        stakedChunks.add(Globals.stakedChunks.create(coordX, coordZ, worldName, uniqueId, firstStaked, lastUpdated));
                    }
                }

                result = new EmbarkerPlayer(uniqueId, stakedChunks);
                cache.put(uniqueId, result);
                results.close();
            }
            statement.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }

        return result;
    }
}
