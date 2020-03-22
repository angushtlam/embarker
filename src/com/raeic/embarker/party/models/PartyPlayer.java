package com.raeic.embarker.party.models;

import com.raeic.embarker.Embarker;
import com.raeic.embarker.db.DB;
import org.bukkit.Bukkit;

import java.sql.*;

public class PartyPlayer {
    private String playerUniqueId;
    private String leaderUniqueId;
    private boolean isPending;

    public PartyPlayer(String playerUniqueId, String leaderUniqueId, boolean isPending) {
        this.playerUniqueId = playerUniqueId;
        this.leaderUniqueId = leaderUniqueId;
        this.isPending = isPending;
    }

    public String getPlayerUniqueId() {
        return playerUniqueId;
    }

    public String getLeaderUniqueId() {
        return leaderUniqueId;
    }

    /**
     * isPending means the player has not confirmed to join the party yet.
     * @return
     */
    public boolean isPending() {
        return isPending;
    }

    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(Embarker.plugin, () -> {
            if (PartyPlayer.findOne(this.playerUniqueId) != null) {
                String sql = "update embarkerpartyplayer " +
                             "set " +
                             "  leaderUniqueId = ?," +
                             "  isPending = ? " +
                             "where " +
                             "  playerUniqueId = ?";

                try {
                    Connection conn = DB.getConnection();
                    PreparedStatement values = conn.prepareStatement(sql);
                    values.setString(1, this.playerUniqueId);
                    values.setBoolean(2, this.isPending);
                    values.setString(3, this.leaderUniqueId);
                    values.executeUpdate();
                    values.closeOnCompletion();

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                String sql = "insert into embarkerpartyplayer(" +
                             "  playerUniqueId, " +
                             "  leaderUniqueId," +
                             "  isPending " +
                             ") values(?, ?, ?)";

                try {
                    Connection conn = DB.getConnection();
                    PreparedStatement values = conn.prepareStatement(sql);
                    values.setString(1, this.playerUniqueId);
                    values.setString(2, this.leaderUniqueId);
                    values.setBoolean(3, this.isPending);
                    values.executeUpdate();
                    values.closeOnCompletion();

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    public static PartyPlayer findOne(String playerUniqueId) {
        String sql = "select " +
                     "  leaderUniqueId, " +
                     "  isPending " +
                     "from embarkerpartyplayer " +
                     "where " +
                     "  playerUniqueId = '" + playerUniqueId + "' " +
                     "limit 1";

        try (Connection conn = DB.getConnection();
             Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sql)) {

            // Should only be one result
            if (results.next()) {
                return new PartyPlayer(playerUniqueId, results.getString("leaderUniqueId"), results.getBoolean("isPending"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }

        return null;
    }

    public static boolean isPlayerLeader(String playerUniqueId) {
        String sql = "select " +
                     "  count(*) as total " +
                     "from embarkerpartyplayer " +
                     "where playerUniqueId = '" + playerUniqueId + "' " +
                     "  and leaderUniqueId = '" + playerUniqueId + "' " +
                     "  and isPending = false " +
                     "limit 1";

        try (Connection conn = DB.getConnection();
             Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sql)) {

            // Should only be one result
            if (results.next()) {
                return results.getInt("total") != 0;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }

        return false;
    }
}
