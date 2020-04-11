package com.raeic.embarker;

import org.junit.jupiter.api.Test;

public class InviteCommandTest extends EmbarkerTest {

    @Test
    void cannotBeRunByServer() {
        var result = server.executeConsole("invite", "fakeplayer");

        result.assertFailed();
        result.assertResponse("This command can only be ran by a player.");
    }
}