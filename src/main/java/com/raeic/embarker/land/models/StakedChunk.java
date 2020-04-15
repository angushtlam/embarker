package com.raeic.embarker.land.models;

import com.raeic.embarker.Globals;
import com.raeic.embarker.db.DB;
import com.raeic.embarker.utils.ModelClass;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class StakedChunk implements ModelClass {
    private int coordX;
    private int coordZ;
    private String worldName;
    private String ownerUniqueId;
    private Timestamp firstStaked;
    private Timestamp lastUpdated;

    public StakedChunk(int coordX, int coordZ, String worldName, String ownerUniqueId, Timestamp firstStaked, Timestamp lastUpdated) {
        this.coordX = coordX;
        this.coordZ = coordZ;
        this.worldName = worldName;
        this.ownerUniqueId = ownerUniqueId;
        this.firstStaked = firstStaked;
        this.lastUpdated = lastUpdated;
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordZ() {
        return coordZ;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getOwnerUniqueId() {
        return ownerUniqueId;
    }

    public void setOwnerUniqueId(String ownerUniqueId) {
        this.ownerUniqueId = ownerUniqueId;
    }

    public void setFirstStaked(Timestamp firstStaked) {
        this.firstStaked = firstStaked;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void delete() {
        // Invalidate caches because it's no longer accurate on write
        String stakedChunkCacheKey = coordX + "," + coordZ + "," + worldName;
        Globals.stakedChunks.invalidateCacheByKey(stakedChunkCacheKey);
        Globals.embarkerPlayers.invalidateCacheByKey(ownerUniqueId);

        Bukkit.getScheduler().runTaskAsynchronously(Globals.plugin, () -> {
            String sql = "delete from embarkerstakedchunk " +
                         "where coordX = ? and coordZ = ? and worldName = ? and ownerUniqueId = ?";

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
        });
    }

    public void save() {
        // Invalidate caches because it's no longer accurate on write
        String stakedChunkCacheKey = coordX + "," + coordZ + "," + worldName;
        Globals.stakedChunks.invalidateCacheByKey(stakedChunkCacheKey);
        Globals.embarkerPlayers.invalidateCacheByKey(ownerUniqueId);

        Bukkit.getScheduler().runTaskAsynchronously(Globals.plugin, () -> {
            String sql = "replace into embarkerstakedchunk(coordX, coordZ, worldName, ownerUniqueId) " +
                         "values(?, ?, ?, ?)";

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
        });
    }
}
