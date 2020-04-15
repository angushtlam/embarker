package com.raeic.embarker.party;

import com.raeic.embarker.db.DB;
import com.raeic.embarker.db.DBTableSetupInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PartyDBTableSetup implements DBTableSetupInterface {
    @Override
    public void setup() {
        String sql = "create table if not exists embarkerpartyplayerinvite ("+
                     "  id int(11) not null auto_increment," +
                     "  senderUniqueId varchar(255) not null," +
                     "  receiverUniqueId varchar(255) not null," +
                     "  firstCreated timestamp not null default current_timestamp()" +
                     ");" +

                     "create table if not exists embarkerpartyplayer (" +
                     "  playerUniqueId varchar(255) NOT NULL primary key," +
                     "  leaderUniqueId varchar(255) NOT NULL," +
                     "  firstCreated timestamp NOT NULL DEFAULT current_timestamp()" +
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
