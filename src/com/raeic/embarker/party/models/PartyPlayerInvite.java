package com.raeic.embarker.party.models;

import com.raeic.embarker.Globals;
import com.raeic.embarker.db.DB;
import org.bukkit.Bukkit;

import java.sql.*;

public class PartyPlayerInvite {
    private String senderUniqueId;
    private String receiverUniqueId;
    private Timestamp firstCreated;

    public PartyPlayerInvite(String senderUniqueId, String receiverUniqueId) {
        this(senderUniqueId, receiverUniqueId, new Timestamp(System.currentTimeMillis()));
    }

    public PartyPlayerInvite(String senderUniqueId, String receiverUniqueId, Timestamp firstCreated) {
        this.senderUniqueId = senderUniqueId;
        this.receiverUniqueId = receiverUniqueId;
        this.firstCreated = firstCreated;
    }

    public Timestamp getFirstCreated() {
        return firstCreated;
    }

    public void setFirstCreated(Timestamp firstCreated) {
        this.firstCreated = firstCreated;
    }

    /**
     * This not only deletes the existing invite, it deletes all invites with the same sender and receiver.
     */
    public void delete() {
        Bukkit.getScheduler().runTaskAsynchronously(Globals.plugin, () -> {
            if (PartyPlayerInvite.findOne(this.senderUniqueId, this.receiverUniqueId) != null) {
                String sql = "delete from embarkerpartyplayerinvite " +
                             "where " +
                             "  senderUniqueId = ? " +
                             "  and receiverUniqueId = ? ";
                try {
                    Connection conn = DB.getConnection();
                    PreparedStatement values = conn.prepareStatement(sql);
                    values.setString(1, this.senderUniqueId);
                    values.setString(2, this.receiverUniqueId);
                    values.executeUpdate();
                    values.closeOnCompletion();

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(Globals.plugin, () -> {
            if (PartyPlayerInvite.findOne(this.senderUniqueId, this.receiverUniqueId) != null) {
                String sql = "update embarkerpartyplayerinvite " +
                             "set " +
                             "  firstCreated = ? " +
                             "where " +
                             "  senderUniqueId = ?" +
                             "  and receiverUniqueId = ?";

                try {
                    Connection conn = DB.getConnection();
                    PreparedStatement values = conn.prepareStatement(sql);
                    values.setTimestamp(1, this.firstCreated);
                    values.setString(2, this.senderUniqueId);
                    values.setString(3, this.receiverUniqueId);
                    values.executeUpdate();
                    values.closeOnCompletion();

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                String sql = "insert into embarkerpartyplayerinvite(" +
                             "  senderUniqueId, " +
                             "  receiverUniqueId, " +
                             "  firstCreated" +
                             ") values(?, ?, ?)";

                try {
                    Connection conn = DB.getConnection();
                    PreparedStatement values = conn.prepareStatement(sql);
                    values.setString(1, this.senderUniqueId);
                    values.setString(2, this.receiverUniqueId);
                    values.setTimestamp(3, this.firstCreated);
                    values.executeUpdate();
                    values.closeOnCompletion();

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    public static PartyPlayerInvite findOne(String senderUniqueId, String receiverUniqueId) {
        String sql = "select " +
                     "  firstCreated " +
                     "from embarkerpartyplayerinvite " +
                     "where " +
                     "  senderUniqueId = '" + senderUniqueId + "' " +
                     "  and receiverUniqueId = '" + receiverUniqueId + "' " +
                     "order by firstCreated desc " +
                     "limit 1";

        PartyPlayerInvite result = null;

        try {
            Connection conn = DB.getConnection();
            Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);

            // Should only be one result
            if (results != null) {
                while (results.next()) {
                    result = new PartyPlayerInvite(senderUniqueId, receiverUniqueId, results.getTimestamp("firstCreated"));
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

    public static void deleteAllInvites(String senderUniqueId) {
        Bukkit.getScheduler().runTaskAsynchronously(Globals.plugin, () -> {
            String sql = "delete from embarkerpartyplayerinvite where senderUniqueId = ?";
            try {
                Connection conn = DB.getConnection();
                PreparedStatement values = conn.prepareStatement(sql);
                values.setString(1, senderUniqueId);
                values.executeUpdate();
                values.closeOnCompletion();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        });
    }
}
