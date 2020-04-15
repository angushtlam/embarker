package com.raeic.embarker.party.models;

import com.raeic.embarker.db.DB;
import com.raeic.embarker.utils.CachedModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class PartyManager extends CachedModel<Party> implements PartyManagerInterface {
    @Override
    public Party create(String leaderUniqueId, ArrayList<String> partyPlayerUniqueIds) {
        Party party;

        if (cache.containsKey(leaderUniqueId)) {
            party = cache.get(leaderUniqueId);
            party.setPartyPlayerUniqueIds(partyPlayerUniqueIds);
        } else {
            party = new Party(leaderUniqueId, partyPlayerUniqueIds);
            cache.put(leaderUniqueId, party);
        }

        return party;
    }

    @Override
    public Party findParty(String playerLookupUniqueId) {
        if (cache.containsKey(playerLookupUniqueId)) {
            return cache.get(playerLookupUniqueId);
        }

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

        String sql = "select playerUniqueId " +
                     "from embarkerpartyplayer " +
                     "where leaderUniqueId = '" + leaderLookupUniqueId + "'";

        try {
            Connection conn = DB.getConnection();
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);

            ArrayList<String> partyPlayerUniqueIds = new ArrayList<>();

            if (results != null) {
                while (results.next()) {
                    partyPlayerUniqueIds.add(results.getString("playerUniqueId"));
                }
                results.close();
            }
            statement.close();

            // Only return a party if there are more than one entry.
            if (partyPlayerUniqueIds.size() > 0) {
                return create(leaderLookupUniqueId, partyPlayerUniqueIds);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }

        return null;
    }
}
