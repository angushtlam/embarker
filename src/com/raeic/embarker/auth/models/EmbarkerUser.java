package com.raeic.embarker.auth.models;


import com.raeic.embarker.db.DB;
import org.bukkit.OfflinePlayer;

import java.sql.*;

public class EmbarkerUser {
    String username;
    String uniqueId;
    Timestamp firstLogin;
    Timestamp latestLogin;

    public EmbarkerUser(String username, String uniqueId, Timestamp firstLogin, Timestamp latestLogin) {
        this.username = username;
        this.uniqueId = uniqueId;
        this.firstLogin = firstLogin;
        this.latestLogin = latestLogin;
    }

    public EmbarkerUser(OfflinePlayer p) {
        this(p.getName(), p.getUniqueId().toString(), new Timestamp(p.getFirstPlayed()), new Timestamp(p.getLastPlayed()));
    }

    public void setFirstLogin(Timestamp firstLogin) {
        this.firstLogin = firstLogin;
    }

    public void setLatestLogin(Timestamp latestLogin) {
        this.latestLogin = latestLogin;
    }

    public static EmbarkerUser findOne(OfflinePlayer p) {
        return EmbarkerUser.findOne(p.getUniqueId().toString());
    }

    public static EmbarkerUser findOne(String uniqueId) {
        String sql = "select username, uniqueId, firstLogin, latestLogin from embarkeruser where uniqueId = '" + uniqueId + "' limit 1";

        String username = null;
        Timestamp firstLogin = null;
        Timestamp latestLogin = null;

        try (Connection conn = DB.getConnection();
             Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sql)) {

            while (results.next()) {
                username = results.getString("username");
                uniqueId = results.getString("uniqueId");
                firstLogin = results.getTimestamp("firstLogin");
                latestLogin = results.getTimestamp("latestLogin");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }

        if (username == null || uniqueId == null || firstLogin == null || latestLogin == null) {
            return null;
        }

        return new EmbarkerUser(username, uniqueId, firstLogin, latestLogin);
    }

    public void save() {
        if (EmbarkerUser.findOne(this.uniqueId) != null) {
            String sql = "update embarkeruser set username = ?, firstLogin = ?, latestLogin = ? where uniqueId = ?";

            try {
                Connection conn = DB.getConnection();
                PreparedStatement values = conn.prepareStatement(sql);
                values.setString(1, this.username);
                values.setTimestamp(2, this.firstLogin);
                values.setTimestamp(3, this.latestLogin);
                values.setString(4, this.uniqueId);
                values.executeUpdate();
                values.closeOnCompletion();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            String sql = "insert into embarkeruser(username, uniqueId, firstLogin, latestLogin) values(?, ?, ?, ?)";

            try {
                Connection conn = DB.getConnection();
                PreparedStatement values = conn.prepareStatement(sql);
                values.setString(1, this.username);
                values.setString(2, this.uniqueId);
                values.setTimestamp(3, this.firstLogin);
                values.setTimestamp(4, this.latestLogin);
                values.executeUpdate();
                values.closeOnCompletion();

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}