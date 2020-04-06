package com.raeic.embarker.party.models;

import com.raeic.embarker.Globals;
import com.raeic.embarker.db.DB;
import org.bukkit.Bukkit;

import java.sql.*;

public class PartyPlayer {
    private String playerUniqueId;
    private String leaderUniqueId;

    public PartyPlayer(String playerUniqueId, String leaderUniqueId) {
        this.playerUniqueId = playerUniqueId;
        this.leaderUniqueId = leaderUniqueId;
    }

    public String getLeaderUniqueId() {
        return leaderUniqueId;
    }

    public void delete() {
        Bukkit.getScheduler().runTaskAsynchronously(Globals.plugin, () -> {
            String sql = "delete from embarkerpartyplayer where playerUniqueId = ?";

            try {
                Connection conn = DB.getConnection();
                PreparedStatement values = conn.prepareStatement(sql);
                values.setString(1, this.playerUniqueId);
                values.executeUpdate();
                values.closeOnCompletion();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        });
    }

    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(Globals.plugin, () -> {
            if (PartyPlayer.findOne(this.playerUniqueId) != null) {
                String sql = "update embarkerpartyplayer " +
                             "set leaderUniqueId = ? " +
                             "where playerUniqueId = ?";

                try {
                    Connection conn = DB.getConnection();
                    PreparedStatement values = conn.prepareStatement(sql);
                    values.setString(1, this.playerUniqueId);
                    values.setString(2, this.leaderUniqueId);
                    values.executeUpdate();
                    values.closeOnCompletion();

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                String sql = "insert into embarkerpartyplayer(" +
                             "  playerUniqueId, " +
                             "  leaderUniqueId" +
                             ") values(?, ?)";

                try {
                    Connection conn = DB.getConnection();
                    PreparedStatement values = conn.prepareStatement(sql);
                    values.setString(1, this.playerUniqueId);
                    values.setString(2, this.leaderUniqueId);
                    values.executeUpdate();
                    values.closeOnCompletion();

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    public static boolean canInviteToParty(String playerUniqueId) {
        PartyPlayer partyPlayer = PartyPlayer.findOne(playerUniqueId);

        // If the player is not in a party, they can invite players to their party.
        if (partyPlayer == null) {
            return true;
        }

        if (PartyPlayer.isPlayerLeader(playerUniqueId)) {
            return true;
        }

        return false;
    }

    public static PartyPlayer findOne(String playerUniqueId) {
        String sql = "select " +
                     "  leaderUniqueId " +
                     "from embarkerpartyplayer " +
                     "where " +
                     "  playerUniqueId = '" + playerUniqueId + "' " +
                     "limit 1";

        PartyPlayer result = null;

        try {
            Connection conn = DB.getConnection();
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);

            // Should only be one result
            if (results != null) {
                while (results.next()) {
                    result = new PartyPlayer(playerUniqueId, results.getString("leaderUniqueId"));
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

    public static boolean isPlayerLeader(String playerUniqueId) {
        String sql = "select " +
                     "  count(*) as total " +
                     "from embarkerpartyplayer " +
                     "where playerUniqueId = '" + playerUniqueId + "' " +
                     "  and leaderUniqueId = '" + playerUniqueId + "' " +
                     "limit 1";

        boolean result = false;

        try {
            Connection conn = DB.getConnection();
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);

            // Should only be one result
            if (results != null) {
                while (results.next()) {
                    result = results.getInt("total") != 0;
                }

                results.close();
            }
            statement.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }

        return result;
    }

    /**
     * This function neither checks if the old nor the new leader is in the existing party.
     * @param oldLeaderUniqueId
     * @param newLeaderUniqueId
     */
    public static void changeLeader(String oldLeaderUniqueId, String newLeaderUniqueId) {
        Bukkit.getScheduler().runTaskAsynchronously(Globals.plugin, () -> {
            String sql = "update embarkerpartyplayer " +
                         "set leaderUniqueId = ? " +
                         "where leaderUniqueId = ?";

            try {
                Connection conn = DB.getConnection();
                PreparedStatement values = conn.prepareStatement(sql);
                values.setString(1, oldLeaderUniqueId);
                values.setString(2, newLeaderUniqueId);
                values.executeUpdate();
                values.closeOnCompletion();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        });
    }
}
