package com.raeic.embarker;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.raeic.embarker.party.models.PartyPlayerInvite;

import org.junit.jupiter.api.Test;

public class JoinCommandTest extends EmbarkerTest {
    @Test
    void requiresArgs() {
        var result = server.executeConsole("join");
        result.assertFailed();
    }

    @Test
    void cannotBeRunByServer() {
        var result = server.executeConsole("join", "fakeplayer");
        result.assertFailed();
        result.assertResponse("This command can only be ran by a player.");
    }

    @Test
    void cannotJoinOwnParty() {
        var inviter = server.addPlayer();
        var result = server.execute("join", inviter, inviter.getName());

        result.assertSucceeded();
        result.assertResponse("You can't join your own party. You're already a part of it.");
        result.assertNoResponse();
    }

    @Test
    void cannotJoinUserNotOnline() {
        var invitee = server.addPlayer();
        var result = server.execute("join", invitee, "fakeplayer");
        result.assertSucceeded();
        result.assertResponse(
                "There are no online players named fakeplayer. To accept an invite both players must be online.");
        result.assertNoResponse();
    }

    @Test
    void cannotJoinInviteeNotInvitedYet() {
        var inviter = server.addPlayer();
        var invitee = server.addPlayer();
        var result = server.execute("join", invitee, inviter.getName());
        result.assertSucceeded();
        result.assertResponse("%s have not sent you an invite to join their party yet.", inviter.getName());
        result.assertResponse("You can ask them to enter /invite %s to invite you.", invitee.getName());
        result.assertNoResponse();
    }

    @Test
    void cannotJoinNeedsConfirm() {
        var inviter = server.addPlayer();
        var invitee = server.addPlayer();
        server.execute("invite", inviter, invitee.getName(), "confirm");
        inviter.assertSaid("Sent an invite to " + invitee.getName() + " to join your party!");
        invitee.assertSaid(inviter.getName() + " invited you to join their party!");

        // Invitee attempts to join
        var result = server.execute("join", invitee, inviter.getName());
        result.assertSucceeded();
        result.assertResponse("You can join %s's party!", inviter.getName());
        result.assertResponse("Enter /join %s confirm to join the party.", inviter.getName());
        result.assertNoResponse();
    }

    @Test
    void joinsPartySuccessfully() {
        var inviter = server.addPlayer();
        var invitee = server.addPlayer();
        server.execute("invite", inviter, invitee.getName(), "confirm");
        inviter.assertSaid("Sent an invite to " + invitee.getName() + " to join your party!");
        invitee.assertSaid(inviter.getName() + " invited you to join their party!");

        // Invitee attempts to join with confirm
        var result = server.execute("join", invitee, inviter.getName(), "confirm");
        result.assertSucceeded();
        result.assertResponse("You joined %s's party!", inviter.getName());
        result.assertNoResponse();
        inviter.assertSaid(invitee.getName() + " just joined your party!");
        inviter.assertNoMoreSaid();
        server.getScheduler().waitAsyncTasksFinished();
        assertNull(PartyPlayerInvite.findOne(inviter.getUniqueId().toString(), invitee.getUniqueId().toString()));
    }

    @Test
    void playerAlreadyInPartyCannotJoinAnother() {
        var oldPartyLeader = server.addPlayer();
        var invitee = server.addPlayer();
        addPlayerToParty(invitee, oldPartyLeader);
        
        // Another player invites player to another party
        var inviter = server.addPlayer();
        server.execute("invite", inviter, invitee.getName(), "confirm");
        inviter.assertSaid("Sent an invite to " + invitee.getName() + " to join your party!");
        invitee.assertSaid(inviter.getName() + " invited you to join their party!");
        
        // Invitee attempts to join with confirm
        var result = server.execute("join", invitee, inviter.getName(), "confirm");
        result.assertSucceeded();
        result.assertResponse("Sorry, you have to leave your existing party before joining a new one.");
        result.assertNoResponse();

        // Does not delete the invite
        assertNotNull(PartyPlayerInvite.findOne(inviter.getUniqueId().toString(), invitee.getUniqueId().toString()));
    }
}