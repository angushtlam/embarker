package com.raeic.embarker.land.models;

import com.raeic.embarker.Globals;
import com.raeic.embarker.db.DB;
import com.raeic.embarker.land.enums.StakeCondition;
import com.raeic.embarker.land.enums.UnstakeCondition;
import com.raeic.embarker.land.utils.ChunkCoord;
import com.raeic.embarker.utils.CachedModel;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;

public class StakedChunkManager extends CachedModel<StakedChunk> implements StakedChunkManagerInterface {
    @Override
    public StakedChunk create(int coordX, int coordZ, String worldName, String ownerUniqueId) {
        return create(coordX, coordZ, worldName, ownerUniqueId, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public StakedChunk create(int coordX, int coordZ, String worldName, String ownerUniqueId, Timestamp firstStaked, Timestamp lastUpdated) {
        String stakedChunkCacheKey = coordX + "," + coordZ + "," + worldName;

        StakedChunk stakedChunk;
        // If the cache contains a staked chunk, update the existing object for the chunk.
        if (cache.containsKey(stakedChunkCacheKey)) {
            stakedChunk = cache.get(stakedChunkCacheKey);
            stakedChunk.setOwnerUniqueId(ownerUniqueId);
            stakedChunk.setFirstStaked(firstStaked);
            stakedChunk.setLastUpdated(lastUpdated);
        } else {
            stakedChunk = new StakedChunk(coordX, coordZ, worldName, ownerUniqueId, firstStaked, lastUpdated);
        }

        return stakedChunk;
    }

    @Override
    public StakedChunk findOne(int coordX, int coordZ, String worldName) {
        String cacheKey = coordX + "," + coordZ + "," + worldName;

        // Check if the cache contains the object. If it does, just return it from the cache.
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        String sql = "select " +
                     "  ownerUniqueId, firstStaked, lastUpdated " +
                     "from embarkerstakedchunk " +
                     "where " +
                     "  coordX = '" + coordX + "' " +
                     "  and coordZ = '" + coordZ + "' " +
                     "  and worldName = '" + worldName + "' " +
                     "limit 1";

        StakedChunk result = null;

        try {
            Connection conn = DB.getConnection();
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);

            if (results != null) {
                // There should only be zero or one row.
                while (results.next()) {
                    String ownerUniqueId = results.getString("ownerUniqueId");
                    Timestamp firstStaked = results.getTimestamp("firstStaked");
                    Timestamp lastUpdated = results.getTimestamp("lastUpdated");
                    result = Globals.stakedChunks.create(coordX, coordZ, worldName, ownerUniqueId, firstStaked, lastUpdated);
                    result.save();
                }

                results.close();
            }
            statement.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }

        return result;
    }

    @Override
    public UnstakeCondition canUnstake(String ownerUniqueId, int coordX, int coordZ, String worldName) {
        // We're building an adjacency list of chunks that exist.
        HashMap<Integer, LinkedList<Integer>> adjacencyList = new HashMap<>();
        StakedChunk[] chunks = Globals.embarkerPlayers.findOne(ownerUniqueId).getStakedChunks();

        // If there are no staked chunks, the player cannot unstake anything.
        if (chunks.length < 1) {
            return UnstakeCondition.NOT_STAKED_UNOWNED;
        }

        // Iterate through all of the chunks to look through
        for (StakedChunk chunk : chunks) {
            // If the chunks are not in the same world they're not contiguous.
            if (!chunk.getWorldName().equals(worldName)) {
                continue;
            }

            if (chunk.getCoordX() == coordX && chunk.getCoordZ() == coordZ) {
                // If the chunk is the only chunk owned, then they can unstake their last chunk.
                if (chunks.length == 1) {
                    return UnstakeCondition.CAN_UNSTAKE;
                }

                continue;
            }

            if (!adjacencyList.containsKey(chunk.getCoordX())) {
                LinkedList<Integer> list = new LinkedList<>();
                list.add(chunk.getCoordZ());
                adjacencyList.put(chunk.getCoordX(), list);
            } else {
                adjacencyList.get(chunk.getCoordX()).add(chunk.getCoordZ());
            }
        }

        // Traverse through the chunk graph to determine if it is still contiguous.
        // If there are islands, the player cannot unstake that chunk.
        // Keep track of how many chunks were traversed. If the BFS does not traverse through the
        // same number of chunks, then we know there are islands.
        int chunksTraveled = 0;

        // Keep track of which chunks are visited in another adjacency list.
        HashMap<Integer, LinkedList<Integer>> visited = new HashMap<>();

        // Build a queue
        LinkedList<ChunkCoord> queue = new LinkedList<>();

        // Get any first chunk coord to start
        int initCoordX = (int) adjacencyList.keySet().toArray()[0];
        queue.add(new ChunkCoord(initCoordX, adjacencyList.get(initCoordX).getFirst()));

        while (!queue.isEmpty()) {
            // Pop queue
            ChunkCoord chunkCoord = queue.poll();

            // If the chunk is already visited, disregard it.
            // This can be optimized later by not adding the visited chunks back into the queue.
            if (visited.containsKey(chunkCoord.coordX)) {
                LinkedList<Integer> zList = visited.get(chunkCoord.coordX);
                if (!zList.contains(chunkCoord.coordZ)) {
                    zList.add(chunkCoord.coordZ);
                    chunksTraveled++;
                } else {
                    // Skip adding adjacent chunks from a visited chunk because it'll recurse indefinitely
                    continue;
                }
            } else {
                LinkedList<Integer> list = new LinkedList<>();
                list.add(chunkCoord.coordZ);
                visited.put(chunkCoord.coordX, list);
                chunksTraveled++;
            }

            // Attempt to add any chunks that are adjacent on the Z axis.
            if (adjacencyList.containsKey(chunkCoord.coordX + 1)) {
                LinkedList<Integer> zList = adjacencyList.get(chunkCoord.coordX + 1);
                if (zList.contains(chunkCoord.coordZ)) {
                    queue.add(new ChunkCoord(chunkCoord.coordX + 1, chunkCoord.coordZ));
                }
            }

            if (adjacencyList.containsKey(chunkCoord.coordX - 1)) {
                LinkedList<Integer> zList = adjacencyList.get(chunkCoord.coordX - 1);
                if (zList.contains(chunkCoord.coordZ)) {
                    queue.add(new ChunkCoord(chunkCoord.coordX - 1, chunkCoord.coordZ));
                }
            }

            // Attempt to add any chunks that are adjacent on the X axis.
            if (adjacencyList.containsKey(chunkCoord.coordX)) {
                LinkedList<Integer> zList = adjacencyList.get(chunkCoord.coordX);
                if (zList.contains(chunkCoord.coordZ + 1)) {
                    queue.add(new ChunkCoord(chunkCoord.coordX, chunkCoord.coordZ + 1));
                }

                if (zList.contains(chunkCoord.coordZ - 1)) {
                    queue.add(new ChunkCoord(chunkCoord.coordX, chunkCoord.coordZ - 1));
                }
            }
        }

        if (chunksTraveled != chunks.length - 1) {
            return UnstakeCondition.NO_ADJACENT;
        }

        return UnstakeCondition.CAN_UNSTAKE;
    }

    @Override
    public StakeCondition canStake(String ownerUniqueId, int coordX, int coordZ, String worldName) {
        if (findOne(coordX, coordZ, worldName) != null) {
            return StakeCondition.ALREADY_STAKED;
        }

        if (Globals.embarkerPlayers.findOne(ownerUniqueId).getStakedChunks().length == 0) {
            return StakeCondition.CAN_STAKE;
        }

        // Check if the chunk otherwise is adjacent to any other chunks the player owns
        for (int x = -1; x <= 1; x++) {
            StakedChunk adjacentXChunk = findOne(coordX + x, coordZ, worldName);
            if (adjacentXChunk != null && ownerUniqueId.equals(adjacentXChunk.getOwnerUniqueId())) {
                return StakeCondition.CAN_STAKE;
            }
        }

        for (int z = -1; z <= 1; z++) {
            StakedChunk adjacentZChunk = findOne(coordX, coordZ + z, worldName);
            if (adjacentZChunk != null && ownerUniqueId.equals(adjacentZChunk.getOwnerUniqueId())) {
                return StakeCondition.CAN_STAKE;
            }
        }

        // Otherwise return that they have no adjacent chunks.
        return StakeCondition.NO_ADJACENT;
    }
}
