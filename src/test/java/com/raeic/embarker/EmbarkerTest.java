package com.raeic.embarker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

/**
 * EmbarkerTest forms a base for most tests. It includes useful setup and tear
 * down functions.
 */
public abstract class EmbarkerTest {
    protected ServerMock server;
    protected Embarker plugin;

    @BeforeEach
    void setup() {
        // TODO put DB in a known state
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Embarker.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unload();
    }
}