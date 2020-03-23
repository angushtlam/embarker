package com.raeic.embarker.land.models;

import com.raeic.embarker.Embarker;
import com.raeic.embarker.land.enums.StakeCondition;
import com.raeic.embarker.land.enums.UnstakeCondition;
import com.raeic.embarker.db.DB;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class StakedChunk {
    int coordX;
    int coordZ;
    String worldName;
    String ownerUniqueId;
    Timestamp firstStaked;
    Timestamp lastUpdated;

    public StakedChunk(int coordX, int coordZ, String worldName, String ownerUniqueId, Timestamp firstStaked, Timestamp lastUpdated) {
        this.coordX = coordX;
        this.coordZ = coordZ;
        this.worldName = worldName;
        this.ownerUniqueId = ownerUniqueId;
        this.firstStaked = firstStaked;
        this.lastUpdated = lastUpdated;
    }

    public StakedChunk(int coordX, int coordZ, String worldName, String ownerUniqueId) {
        this(coordX, coordZ, worldName, ownerUniqueId, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
    }

    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(Embarker.plugin, () -> {
            if (StakedChunk.findOne(this.coordX, this.coordZ, this.worldName) != null) {
                String sql = "update embarkerstakedchunk set ownerUniqueId = ?, lastUpdated = ? where coordX = ? and coordZ = ? and worldName = ?";

                try {
                    Connection conn = DB.getConnection();
                    PreparedStatement values = conn.prepareStatement(sql);
                    values.setString(1, this.ownerUniqueId);
                    values.setTimestamp(2, this.lastUpdated);
                    values.setInt(3, this.coordX);
                    values.setInt(4, this.coordZ);
                    values.setString(5, this.worldName);
                    values.executeUpdate();
                    values.closeOnCompletion();

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                String sql = "insert into embarkerstakedchunk(coordX, coordZ, worldName, ownerUniqueId) values(?, ?, ?, ?)";

                try {
                    Connection conn = DB.getConnection();
                    PreparedStatement values = conn.prepareStatement(sql);
                    values.setInt(1, this.coordX);
                    values.setInt(2, this.coordZ);
                    values.setString(3, this.worldName);
                    values.setString(4, this.ownerUniqueId);
                    values.executeUpdate();
                    values.closeOnCompletion();

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    public void delete() {
        Bukkit.getScheduler().runTaskAsynchronously(Embarker.plugin, () -> {
            if (StakedChunk.findOne(this.coordX, this.coordZ, this.worldName) != null) {
                String sql = "delete from embarkerstakedchunk where coordX = ? and coordZ = ? and worldName = ? and ownerUniqueId = ?";

                try {
                    Connection conn = DB.getConnection();
                    PreparedStatement values = conn.prepareStatement(sql);
                    values.setInt(1, this.coordX);
                    values.setInt(2, this.coordZ);
                    values.setString(3, this.ownerUniqueId);
                    values.setString(4, this.worldName);
                    values.executeUpdate();
                    values.closeOnCompletion();

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    public String getOwnerUniqueId() {
        return this.ownerUniqueId;
    }

    private String getWorldName() {
        return this.worldName;
    }

    public static StakedChunk findOne(int coordX, int coordZ, String worldName) {
        String sql = "select ownerUniqueId, firstStaked, lastUpdated from embarkerstakedchunk where coordX = '" + coordX + "' and coordZ = '" + coordZ + "' and worldName = '" + worldName + "' limit 1";

        StakedChunk result = null;

        try {
            Connection conn = DB.getConnection();
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);

            // There should only be zero or one row.
            while (results != null && results.next()) {
                String ownerUniqueId = results.getString("ownerUniqueId");
                Timestamp firstStaked = results.getTimestamp("firstStaked");
                Timestamp lastUpdated = results.getTimestamp("lastUpdated");
                result = new StakedChunk(coordX, coordZ, worldName, ownerUniqueId, firstStaked, lastUpdated);
            }

            results.close();
            statement.closeOnCompletion();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }

        return result;
    }


    public static ArrayList<StakedChunk> findAllOwnedBy(String ownerUniqueId) {
        String sql = "select coordX, coordZ, worldName, firstStaked, lastUpdated from embarkerstakedchunk where ownerUniqueId = '" + ownerUniqueId + "'";

        ArrayList<StakedChunk> chunks = new ArrayList<>();

        try {
            Connection conn = DB.getConnection();
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);

            while (results != null && results.next()) {
                int coordX = results.getInt("coordX");
                int coordZ = results.getInt("coordZ");
                String worldName = results.getString("worldName");
                Timestamp firstStaked = results.getTimestamp("firstStaked");
                Timestamp lastUpdated = results.getTimestamp("lastUpdated");
                chunks.add(new StakedChunk(coordX, coordZ, worldName, ownerUniqueId, firstStaked, lastUpdated));
            }

            results.close();
            statement.closeOnCompletion();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return new ArrayList<>();
        }

        return chunks;
    }

    public static int countOwnedBy(String ownerUniqueId) {
        String sql = "select count(*) as total from embarkerstakedchunk where ownerUniqueId = '" + ownerUniqueId + "'";

        int total = 0;

        try {
            Connection conn = DB.getConnection();
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);

            while (results != null && results.next()) {
                total = results.getInt("total");
            }

            results.close();
            statement.closeOnCompletion();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return -1;
        }

        return total;
    }

    public static StakeCondition canStake(String ownerUniqueId, int coordX, int coordZ, String worldName) {
        if (StakedChunk.findOne(coordX, coordZ, worldName) != null) {
            return StakeCondition.ALREADY_STAKED;
        }

        if (StakedChunk.countOwnedBy(ownerUniqueId) == 0) {
            return StakeCondition.CAN_STAKE;
        }

        String sql = "select count(*) as total from embarkerstakedchunk "
                     + "where "
                     + "  ownerUniqueId = '" + ownerUniqueId + "' "
                     + "  and worldName = '" + worldName + "' "
                     + "  and ("
                     + "    (coordX = " + coordX + " and (coordZ = " + (coordZ - 1) + " or coordZ = " + (coordZ + 1) + "))"
                     + "    or (coordZ = " + coordZ + " and (coordX = " + (coordX - 1) + " or coordX = " + (coordX + 1) + "))"
                     + "  )";

        int total = 0;

        try {
            Connection conn = DB.getConnection();
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);

            while (results != null && results.next()) {
                total = results.getInt("total");
            }

            results.close();
            statement.closeOnCompletion();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return StakeCondition.DATABASE_ERROR;
        }

        if (total >= 1) {
            return StakeCondition.CAN_STAKE;
        }

        return StakeCondition.NO_ADJACENT;
    }

    public static UnstakeCondition canUnstake(String ownerUniqueId, int coordX, int coordZ, String worldName) {
        // We're building an adjacency list of chunks that exist.
        HashMap<Integer, LinkedList<Integer>> adjacencyList = new HashMap<>();
        ArrayList<StakedChunk> chunks = StakedChunk.findAllOwnedBy(ownerUniqueId);

        // If there are no staked chunks, the player cannot unstake anything.
        if (chunks.size() < 1) {
            return UnstakeCondition.NOT_STAKED_UNOWNED;
        }

        // Iterate through all of the chunks to look through
        for (StakedChunk chunk : chunks) {
            // If the chunks are not in the same world they're not contiguous.
            if (!chunk.getWorldName().equals(worldName)) {
                continue;
            }

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
