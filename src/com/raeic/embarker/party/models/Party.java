package com.raeic.embarker.party.models;

import com.raeic.embarker.auth.models.EmbarkerUser;
import com.raeic.embarker.db.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Party {
    String leaderUniqueId;
    ArrayList<String> partyPlayerUniqueIds;

    private Party(String leaderUniqueId, ArrayList<String> partyPlayerUniqueIds) {
        this.leaderUniqueId = leaderUniqueId;
        this.partyPlayerUniqueIds = partyPlayerUniqueIds;
    }

    public String getLeaderUniqueId() {
        return leaderUniqueId;
    }

    /**
     * Party player list includes leader unique ID.
     * @return
     */
    public ArrayList<String> getPartyPlayerUniqueIds() {
        return partyPlayerUniqueIds;
    }

    public static Party findParty(String playerLookupUniqueId) {
        // If the player does not belong to a party or they are still pending, they do not belong to a party.
        PartyPlayer player = PartyPlayer.findOne(playerLookupUniqueId);
        if (player == null || player.isPending()) {
            return null;
        }

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
                     "  leaderUniqueId = '" + leaderLookupUniqueId + "' " +
                     "  and isPending = false";

        try (Connection conn = DB.getConnection();
             Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sql)) {

            ArrayList<String> partyPlayerUniqueIds = new ArrayList<>();

            while (results.next()) {
                partyPlayerUniqueIds.add(results.getString("playerUniqueId"));
            }

            // Only return a party if there are more than one entry.
            if (partyPlayerUniqueIds.size() > 0) {
                return new Party(playerLookupUniqueId, partyPlayerUniqueIds);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }

        return null;
    }
}
