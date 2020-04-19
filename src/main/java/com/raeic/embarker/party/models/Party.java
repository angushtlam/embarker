package com.raeic.embarker.party.models;

import com.raeic.embarker.Globals;
import com.raeic.embarker.auth.models.EmbarkerUser;
import com.raeic.embarker.db.DB;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

public class Party {
    String leaderUniqueId;
    ArrayList<String> partyPlayerUniqueIds;

    public Party(String leaderUniqueId, ArrayList<String> partyPlayerUniqueIds) {
        this.leaderUniqueId = leaderUniqueId;
        this.partyPlayerUniqueIds = partyPlayerUniqueIds;
    }

    public void disband() {
        Bukkit.getScheduler().runTaskAsynchronously(Globals.plugin, () -> {
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

            // Invalidate the cache synchronously when the party doesn't exist anymore
            Bukkit.getScheduler().runTask(Globals.plugin, () -> {
                Globals.party.invalidateCacheByKey(leaderUniqueId);
            });
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

    public void setPartyPlayerUniqueIds(ArrayList<String> partyPlayerUniqueIds) {
        this.partyPlayerUniqueIds = partyPlayerUniqueIds;
    }

    public ArrayList<String> getPartyPlayersName() {
        ArrayList<String> memberNames = new ArrayList<>();

        for (String memberUniqueId : this.getPartyPlayersUniqueId()) {
            EmbarkerUser u = EmbarkerUser.findOne(memberUniqueId);
            if (u != null) {
                memberNames.add(u.getUsername());
            }
        }

        Collections.sort(memberNames);
        return memberNames;
    }
}
