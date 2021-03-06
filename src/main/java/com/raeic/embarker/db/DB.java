package com.raeic.embarker.db;

import com.raeic.embarker.auth.AuthDBTableSetup;
import com.raeic.embarker.land.LandDBTableSetup;
import com.raeic.embarker.party.PartyDBTableSetup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static String url;
    private static String username;
    private static String password;

    private static Connection cachedConnection = null;

    public static void setupConnection(String url, String username, String password) {
        DB.url = url;
        DB.username = username;
        DB.password = password;
    }

    public static void setupTables() {
        new AuthDBTableSetup().setup();
        new LandDBTableSetup().setup();
        new PartyDBTableSetup().setup();
    }

    public static Connection getConnection(int retries) throws SQLException {
        // We're gonna reuse the same connection. Try not to close it.
        try {
            if (DB.cachedConnection == null || DB.cachedConnection.isClosed()) {
                DB.cachedConnection = DriverManager.getConnection(DB.url, DB.username, DB.password);
            }
        } catch (SQLException e) {
            if (retries == 0) {
                throw e;
            }

            return getConnection(retries - 1);
        }

        return DB.cachedConnection;
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(3);
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
