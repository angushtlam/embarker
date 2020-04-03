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
    private boolean isDeleted;
    private int coordX;
    private int coordZ;
    private String worldName;
    private String ownerUniqueId;
    private Timestamp firstStaked;
    private Timestamp lastUpdated;

    public StakedChunk(int coordX, int coordZ, String worldName, String ownerUniqueId, Timestamp firstStaked, Timestamp lastUpdated) {
        this.isDeleted = false;
        this.coordX = coordX;
        this.coordZ = coordZ;
        this.worldName = worldName;
        this.ownerUniqueId = ownerUniqueId;
        this.firstStaked = firstStaked;
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getCoordX() {
        return coordX;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public int getCoordZ() {
        return coordZ;
    }

    public void setCoordZ(int coordZ) {
        this.coordZ = coordZ;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
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

    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(Globals.plugin, () -> {
            if (Globals.stakedChunks.findOne(this.coordX, this.coordZ, this.worldName) != null) {
                String sql = "update embarkerstakedchunk " +
                             "set " +
                             "  ownerUniqueId = ?, " +
                             "  lastUpdated = ? " +
                             "where " +
                             "  coordX = ? " +
                             "  and coordZ = ? " +
                             "  and worldName = ?";

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
                String sql = "insert into embarkerstakedchunk(coordX, coordZ, worldName, ownerUniqueId) " +
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
            }
        });
    }

    public void delete() {
        this.isDeleted = true;

        Bukkit.getScheduler().runTaskAsynchronously(Globals.plugin, () -> {
            if (Globals.stakedChunks.findOne(this.coordX, this.coordZ, this.worldName) != null) {
                String sql = "delete from embarkerstakedchunk " +
                             "where coordX = ? and coordZ = ? and worldName = ? and ownerUniqueId = ?";

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
}
