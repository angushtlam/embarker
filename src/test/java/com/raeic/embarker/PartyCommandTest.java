package com.raeic.embarker;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

import com.raeic.embarker.auth.models.EmbarkerUser;

import org.junit.jupiter.api.Test;

public class PartyCommandTest extends EmbarkerTest {
    @Test
    void cannotBeRunByServer() {
        var result = server.executeConsole("party");
        result.assertFailed();
        result.assertResponse("This command can only be ran by a player.");
    }

    @Test
    void runCommandNotInParty() {
        var player = server.addPlayer();

        var result = server.execute("party", player);
        result.assertSucceeded();
        result.assertResponse("You do not have a party. Invite people with /invite <player>!");
        result.assertResponse("Party members share each other's staked lands.");
        result.assertNoResponse();
    }

    @Test
    void runCommandAsLeader() {
        var now = new Timestamp(Instant.now().toEpochMilli());
        var partyLeader = server.addPlayer();
        new EmbarkerUser(partyLeader.getName(), partyLeader.getUniqueId().toString(), now, now).save();
        var partyFollower = server.addPlayer();
        new EmbarkerUser(partyFollower.getName(), partyFollower.getUniqueId().toString(), now, now).save();
        addPlayerToParty(partyFollower, partyLeader);

        var result = server.execute("party", partyLeader);
        result.assertSucceeded();
        result.assertResponse("You are the leader of your party.");
        result.assertResponse("You can leave your party with /leave, " + 
                              "disband it with /disband, " +
                              "invite a new player with /invite <player>, " +
                              "and dismiss another player with /dismiss <player>.");

        String[] partyMembers = {partyLeader.getName(), partyFollower.getName()};
        Arrays.sort(partyMembers);
        result.assertResponse("Your party consists of: " +  String.join(", ", partyMembers));
        result.assertNoResponse();
    }

    @Test
    void runCommandAsFollower() {
        var now = new Timestamp(Instant.now().toEpochMilli());
        var partyLeader = server.addPlayer();
        new EmbarkerUser(partyLeader.getName(), partyLeader.getUniqueId().toString(), now, now).save();
        var partyFollower = server.addPlayer();
        new EmbarkerUser(partyFollower.getName(), partyFollower.getUniqueId().toString(), now, now).save();
        addPlayerToParty(partyFollower, partyLeader);

        var result = server.execute("party", partyFollower);
        result.assertSucceeded();
        result.assertResponse("You are in " + partyLeader.getName() + "'s party.");
        result.assertResponse("You can leave your party with /leave.");

        String[] partyMembers = {partyLeader.getName(), partyFollower.getName()};
        Arrays.sort(partyMembers);
        result.assertResponse("Your party consists of: " +  String.join(", ", partyMembers));
        result.assertNoResponse();
    }

}