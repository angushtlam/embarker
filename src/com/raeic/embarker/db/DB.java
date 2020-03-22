package com.raeic.embarker.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static String url;
    private static String username;
    private static String password;

    private static Connection cachedConnection = null;

    public static void setup(String url, String username, String password) {
        DB.url = url;
        DB.username = username;
        DB.password = password;
    }

    public static Connection getConnection() throws SQLException {
        // We're gonna reuse the same connection. Try not to close it.
        if (DB.cachedConnection == null || DB.cachedConnection.isClosed()) {
            DB.cachedConnection = DriverManager.getConnection(DB.url, DB.username, DB.password);
        }

        return DB.cachedConnection;
    }

    public static void closeConnection() {
        try {
            if (DB.cachedConnection != null && !DB.cachedConnection.isClosed()) {
                DB.cachedConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
