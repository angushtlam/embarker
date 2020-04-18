package com.raeic.embarker.land;

import com.raeic.embarker.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LandDBTableSetup {
    public void setup() {
        String sql = "create table if not exists embarkerstakedchunk (" +
                     "  coordX int(11) not null," +
                     "  coordZ int(11) not null," +
                     "  worldName varchar(255) not null," +
                     "  ownerUniqueId varchar(255) not null," +
                     "  firstStaked timestamp not null default current_timestamp()," +
                     "  lastUpdated timestamp not null default current_timestamp()," +
                     "  constraint chunkUniqueKey primary key (coordX, coordZ)" +
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
