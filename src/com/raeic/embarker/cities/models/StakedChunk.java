package com.raeic.embarker.cities.models;

import com.raeic.embarker.cities.enums.StakeCondition;
import com.raeic.embarker.cities.enums.UnstakeCondition;
import com.raeic.embarker.db.DB;
import org.bukkit.Chunk;

import java.sql.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

public class StakedChunk {
    int coordX;
    int coordZ;
    String ownerUniqueId;
    Timestamp firstStaked;
    Timestamp lastUpdated;

    public StakedChunk(int coordX, int coordZ, String ownerUniqueId, Timestamp firstStaked, Timestamp lastUpdated) {
        this.coordX = coordX;
        this.coordZ = coordZ;
        this.ownerUniqueId = ownerUniqueId;
        this.firstStaked = firstStaked;
        this.lastUpdated = lastUpdated;
    }

    public StakedChunk(int coordX, int coordZ, String ownerUniqueId) {
        this(coordX, coordZ, ownerUniqueId, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
    }

    public void save() {
        if (StakedChunk.findOne(this.coordX, this.coordZ) != null) {
            String sql = "update embarkerstakedchunk set ownerUniqueId = ?, lastUpdated = ? where coordX = ? and coordZ = ?";

            try (Connection conn = DB.getConnection();
                 PreparedStatement values = conn.prepareStatement(sql)) {
                values.setString(1, this.ownerUniqueId);
                values.setTimestamp(2, this.lastUpdated);
                values.setInt(3, this.coordX);
                values.setInt(4, this.coordZ);
                values.executeUpdate();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            String sql = "insert into embarkerstakedchunk(coordX, coordZ, ownerUniqueId) values(?, ?, ?)";

            try (Connection conn = DB.getConnection();
                 PreparedStatement values = conn.prepareStatement(sql)) {
                values.setInt(1, this.coordX);
                values.setInt(2, this.coordZ);
                values.setString(3, this.ownerUniqueId);
                values.executeUpdate();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void delete() {
        if (StakedChunk.findOne(this.coordX, this.coordZ) != null) {
            String sql = "delete from embarkerstakedchunk where coordX = ? and coordZ = ? and ownerUniqueId = ?";

            try (Connection conn = DB.getConnection();
                 PreparedStatement values = conn.prepareStatement(sql)) {
                values.setInt(1, this.coordX);
                values.setInt(2, this.coordZ);
                values.setString(3, this.ownerUniqueId);
                values.executeUpdate();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public String getOwnerUniqueId() {
        return this.ownerUniqueId;
    }

    public static StakedChunk findOne(int coordX, int coordZ) {
        String sql = "select ownerUniqueId, firstStaked, lastUpdated from embarkerstakedchunk where coordX = '" + coordX + "' and coordZ = '" + coordZ + "' limit 1";

        String ownerUniqueId = null;
        Timestamp firstStaked = null;
        Timestamp lastUpdated = null;

        try (Connection conn = DB.getConnection();
             Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sql)) {

            while (results.next()) {
                ownerUniqueId = results.getString("ownerUniqueId");
                firstStaked = results.getTimestamp("firstStaked");
                lastUpdated = results.getTimestamp("lastUpdated");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }

        if (ownerUniqueId == null) {
            return null;
        }

        return new StakedChunk(coordX, coordZ, ownerUniqueId, firstStaked, lastUpdated);
    }


    public static ArrayList<StakedChunk> findAllOwnedBy(String ownerUniqueId) {
        String sql = "select coordX, coordZ, firstStaked, lastUpdated from embarkerstakedchunk where ownerUniqueId = '" + ownerUniqueId + "'";

        int coordX = 0;
        int coordZ = 0;
        Timestamp firstStaked = null;
        Timestamp lastUpdated = null;

        ArrayList<StakedChunk> chunks = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sql)) {

            while (results.next()) {
                coordX = results.getInt("coordX");
                coordZ = results.getInt("coordZ");
                firstStaked = results.getTimestamp("firstStaked");
                lastUpdated = results.getTimestamp("lastUpdated");

                chunks.add(new StakedChunk(coordX, coordZ, ownerUniqueId, firstStaked, lastUpdated));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return new ArrayList<>();
        }

        return chunks;
    }

    public static int countOwnedBy(String ownerUniqueId) {
        String sql = "select count(*) as total from embarkerstakedchunk where ownerUniqueId = '" + ownerUniqueId + "'";

        int total = 0;

        try (Connection conn = DB.getConnection();
             Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sql)) {

            while (results.next()) {
                total = results.getInt("total");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return -1;
        }

        return total;
    }

    public static StakeCondition canStake(String ownerUniqueId, int coordX, int coordZ) {
        if (StakedChunk.findOne(coordX, coordZ) != null) {
            return StakeCondition.ALREADY_STAKED;
        }

        if (StakedChunk.countOwnedBy(ownerUniqueId) == 0) {
            return StakeCondition.CAN_STAKE;
        }

        String sql = "select count(*) as total from embarkerstakedchunk where ownerUniqueId = '" + ownerUniqueId + "' and ("
                           + "(coordX = " + coordX + " and (coordZ = " + (coordZ - 1) + " or coordZ = " + (coordZ + 1) + "))"
                           + "or (coordZ = " + coordZ + " and (coordX = " + (coordX - 1) + " or coordX = " + (coordX + 1) + "))"
                           + ")";

        int total = 0;

        try (Connection conn = DB.getConnection();
             Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sql)) {

            while (results.next()) {
                total = results.getInt("total");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return StakeCondition.DATABASE_ERROR;
        }

        if (total >= 1) {
            return StakeCondition.CAN_STAKE;
        }

        return StakeCondition.NO_ADJACENT;
    }

    public static UnstakeCondition canUnstake(String ownerUniqueId, int coordX, int coordZ) {
        // We're building an adjacency list of chunks that exist.
        HashMap<Integer, LinkedList<Integer>> adjacencyList = new HashMap<>();
        ArrayList<StakedChunk> chunks = StakedChunk.findAllOwnedBy(ownerUniqueId);

        // If there are no staked chunks, the player cannot unstake anything.
        if (chunks.size() < 1) {
            return UnstakeCondition.NOT_STAKED_UNOWNED;
        }

        // Iterate through all of the chunks to find
        for (StakedChunk chunk : chunks) {
            if (chunk.coordX == coordX && chunk.coordZ == coordZ) {
                // If the chunk is the only chunk owned, then they can unstake their last chunk.
                if (chunks.size() == 1) {
                    return UnstakeCondition.CAN_UNSTAKE;
                }

                continue;
            }

            if (!adjacencyList.containsKey(chunk.coordX)) {
                LinkedList<Integer> list = new LinkedList<>();
                list.add(chunk.coordZ);
                adjacencyList.put(chunk.coordX, list);
            } else {
                adjacencyList.get(chunk.coordX).add(chunk.coordZ);
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

        if (chunksTraveled != chunks.size() - 1) {
            return UnstakeCondition.NO_ADJACENT;
        }

        return UnstakeCondition.CAN_UNSTAKE;
    }

    private static class ChunkCoord {
        int coordX;
        int coordZ;

        private ChunkCoord(int coordX, int coordZ) {
            this.coordX = coordX;
            this.coordZ = coordZ;
        }
    }
}
