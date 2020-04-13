package com.raeic.embarker;

import static org.junit.jupiter.api.Assertions.*;

import com.raeic.embarker.party.models.PartyPlayerInvite;

import org.junit.jupiter.api.Test;

public class InviteCommandTest extends EmbarkerTest {

    @Test
    void requiresArgs() {
        var result = server.executeConsole("invite");

        result.assertFailed();
    }

    @Test
    void cannotBeRunByServer() {
        var result = server.executeConsole("invite", "fakeplayer");

        result.assertFailed();
        result.assertResponse("This command can only be ran by a player.");
    }

    @Test
    void cannotInviteYourself() {
        var inviter = server.addPlayer();
        var result = server.execute("invite", inviter, inviter.getName());

        result.assertSucceeded();
        result.assertResponse("You can't invite yourself.");
        result.assertNoResponse();
    }

    @Test
    void requiresConfirm() {
        var inviter = server.addPlayer();
        var invitee = server.addPlayer();
        var result = server.execute("invite", inviter, invitee.getName());

        result.assertSucceeded();
        result.assertResponse("You can invite %s to your party!", invitee.getName());
        result.assertResponse("Enter /invite %s confirm to send your invite.", invitee.getName());
        result.assertNoResponse();
    }

    @Test
    void createsAnInviteOnPlayer() {
        var inviter = server.addPlayer();
        var invitee = server.addPlayer();
        var result = server.execute("invite", inviter, invitee.getName(), "confirm");

        result.assertSucceeded();
        result.assertResponse("Sent an invite to %s to join your party!", invitee.getName());
        result.assertNoResponse();
        invitee.assertSaid(inviter.getName() + " invited you to join their party!");
        invitee.assertNoMoreSaid();
        server.getScheduler().waitAsyncTasksFinished();
        var ppi = PartyPlayerInvite.findOne(inviter.getUniqueId().toString(), invitee.getUniqueId().toString());
        assertNotNull(ppi);
    }

    @Test
    void playerInPartyCannotInvite() {
        var partyLeader = server.addPlayer();
        var inviter = server.addPlayer();
        var invitee = server.addPlayer();
        addPlayerToParty(inviter, partyLeader);

        var result = server.execute("invite", inviter, invitee.getName(), "confirm");

        result.assertSucceeded();
        result.assertResponse("Sorry, only party leaders can invite new members to the party.");
        result.assertNoResponse();
    }
}