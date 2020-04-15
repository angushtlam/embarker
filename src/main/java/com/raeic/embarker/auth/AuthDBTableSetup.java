package com.raeic.embarker.auth;

import com.raeic.embarker.db.DB;
import com.raeic.embarker.db.DBTableSetupInterface;

import java.sql.*;

public class AuthDBTableSetup implements DBTableSetupInterface {
    @Override
    public void setup() {
        String sql = "create table if not exists embarkeruser (" +
                     "  uniqueId varchar(255) not null primary key," +
                     "  username varchar(255) not null," +
                     "  firstLogin timestamp not null default current_timestamp()," +
                     "  latestLogin timestamp not null default current_timestamp()" +
                     ");";

        try {
            Connection conn = DB.getConnection();
            PreparedStatement values = conn.prepareStatement(sql);
            values.executeUpdate();
            values.closeOnCompletion();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
