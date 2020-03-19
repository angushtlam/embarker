package com.raeic.embarker.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static String url;
    private static String username;
    private static String password;

    public static void setup(String url, String username, String password) {
        DB.url = url;
        DB.username = username;
        DB.password = password;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB.url, DB.username, DB.password);
    }
}
