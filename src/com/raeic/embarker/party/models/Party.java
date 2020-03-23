package com.raeic.embarker.party.models;

import com.raeic.embarker.Embarker;
import com.raeic.embarker.db.DB;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class Party {
    String leaderUniqueId;
    ArrayList<String> partyPlayerUniqueIds;

    private Party(String leaderUniqueId, ArrayList<String> partyPlayerUniqueIds) {
        this.leaderUniqueId = leaderUniqueId;
        this.partyPlayerUniqueIds = partyPlayerUniqueIds;
    }

    public void disband() {
        Bukkit.getScheduler().runTaskAsynchronously(Embarker.plugin, () -> {
            String sql = "delete from embarkerpartyplayer where leaderUniqueId = ?";
            try {
                Connection conn = DB.getConnection();
                PreparedStatement values = conn.prepareStatement(sql);
                values.setString(1, this.leaderUniqueId);
                values.executeUpdate();
                values.closeOnCompletion();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        });
    }

    public String getLeaderUniqueId() {
        return leaderUniqueId;
    }

    /**
     * Party player list includes leader unique ID.
     *
     * @return
     */
    public ArrayList<String> getPartyPlayersUniqueId() {
        return partyPlayerUniqueIds;
    }

    public ArrayList<String> getPartyPlayersName() {
        ArrayList<String> memberNames = new ArrayList<>();

        for (String memberUniqueId : this.getPartyPlayersUniqueId()) {
            OfflinePlayer memberPlayer = Bukkit.getOfflinePlayer(UUID.fromString(memberUniqueId));
            memberNames.add(memberPlayer.getName());
        }

        return memberNames;
    }

    public static Party findParty(String playerLookupUniqueId) {
        // Check if the player belong to a party first
        PartyPlayer player = PartyPlayer.findOne(playerLookupUniqueId);
        if (player == null) {
            return null;
        }

        // Query by the party leader unique ID.
        String leaderLookupUniqueId;
        if (PartyPlayer.isPlayerLeader(playerLookupUniqueId)) {
            leaderLookupUniqueId = playerLookupUniqueId;
        } else {
            leaderLookupUniqueId = player.getLeaderUniqueId();
        }

        String sql = "select " +
                     "  playerUniqueId " +
                     "from embarkerpartyplayer " +
                     "where " +
                     "  leaderUniqueId = '" + leaderLookupUniqueId + "' ";

        try (Connection conn = DB.getConnection();
             Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sql)) {

            ArrayList<String> partyPlayerUniqueIds = new ArrayList<>();

            while (results != null && results.next()) {
                partyPlayerUniqueIds.add(results.getString("playerUniqueId"));
            }

            // Only return a party if there are more than one entry.
            if (partyPlayerUniqueIds.size() > 0) {
                return new Party(leaderLookupUniqueId, partyPlayerUniqueIds);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }

        return null;
    }
}
