package com.raeic.embarker;

import java.sql.Connection;
import java.sql.SQLException;

import com.raeic.embarker.db.DB;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.EntityMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

/**
 * EmbarkerTest forms a base for most tests. It includes useful setup and tear
 * down functions.
 */
public abstract class EmbarkerTest {
    protected ServerMock server;
    protected Embarker plugin;

    @BeforeEach
    void setup() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Embarker.class);
        setupDatabase();
    }

    @AfterEach
    void tearDown() {
        tearDownDatabase();
        MockBukkit.unload();
    }

    /**
     * prepareDatabase puts the database into a consistent state before each test.
     */
    private void setupDatabase() {
        try {
            var conn = DB.getConnection();
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            conn.createStatement().execute("start transaction");
        } catch (SQLException ex) {
            fail(ex);
        }
    }

    /**
     * tearDownDatabase restores the database to its former state before running the
     * state.
     */
    private void tearDownDatabase() {
        try {
            var conn = DB.getConnection();
            conn.createStatement().execute("rollback");
        } catch (SQLException ex) {
            fail(ex);
        }
    }

    /**
     * addPlayerToParty sets it up so that joiner is in partyLeader's party. It
     * invites joiner then accepts the invitation.
     */
    void addPlayerToParty(PlayerMock joiner, PlayerMock partyLeader) {
        server.execute("invite", partyLeader, joiner.getName(), "confirm").assertSucceeded();
        server.getScheduler().waitAsyncTasksFinished();
        server.execute("join", joiner, partyLeader.getName(), "confirm").assertSucceeded();
        server.getScheduler().waitAsyncTasksFinished();
        clearMessages(joiner);
        clearMessages(partyLeader);
    }

    /**
     * clearMessages empties MockBukkit's history of messages sent to a player. This
     * is useful when you don't want to assert on these messages, but plan on
     * sending more messages later on.
     */
    void clearMessages(EntityMock entity) {
        while (entity.nextMessage() != null) {
        }
    }
}