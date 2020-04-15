package com.raeic.embarker.party;

import com.raeic.embarker.db.DB;
import com.raeic.embarker.db.DBTableSetupInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PartyDBTableSetup implements DBTableSetupInterface {
    @Override
    public void setup() {
        // JBDC really doesn't like creating multiple tables in one query.
        String sql = "create table if not exists embarkerpartyplayerinvite ("+
                     "  id int(11) not null auto_increment primary key," +
                     "  senderUniqueId varchar(255) not null," +
                     "  receiverUniqueId varchar(255) not null," +
                     "  firstCreated timestamp not null default current_timestamp()" +
                     ");";

        String sql2 = "create table if not exists embarkerpartyplayer (" +
                     "  playerUniqueId varchar(255) not null primary key," +
                     "  leaderUniqueId varchar(255) not null," +
                     "  firstCreated timestamp not null default current_timestamp()" +
                     ");";

        try {
            Connection conn = DB.getConnection();

            PreparedStatement values = conn.prepareStatement(sql);
            values.executeUpdate();
            values.closeOnCompletion();

            PreparedStatement values2 = conn.prepareStatement(sql2);
            values2.executeUpdate();
            values2.closeOnCompletion();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
