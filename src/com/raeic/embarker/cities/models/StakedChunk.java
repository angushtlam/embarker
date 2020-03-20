package com.raeic.embarker.cities.models;

import com.raeic.embarker.db.DB;

import java.sql.*;

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

}
