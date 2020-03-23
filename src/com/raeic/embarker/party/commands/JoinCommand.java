package com.raeic.embarker.party.commands;

import com.google.common.collect.ImmutableList;
import com.raeic.embarker.party.models.Party;
import com.raeic.embarker.party.models.PartyPlayer;
import com.raeic.embarker.party.models.PartyPlayerInvite;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;

public class JoinCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 2 || args.length < 1 || (args.length == 2 && !args[1].equalsIgnoreCase("confirm"))) {
            sender.sendMessage("Usage: /join <player> (confirm)");
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran by a player.");
            return false;
        }

        Player p = (Player) sender;
        String playerNameToJoin = args[0];

        // Check if the player name matches with anyone online.
        List<Player> onlinePlayers = ImmutableList.copyOf(Bukkit.getOnlinePlayers());

        for (Player senderPlayer : onlinePlayers) {
            // Check to see if the sender player is online.
            if (!senderPlayer.getName().equalsIgnoreCase(playerNameToJoin)) {
                continue;
            }

            if (senderPlayer.getName().equalsIgnoreCase(p.getName())) {
                p.sendMessage("You can't join your own party. You're already a part of it.");
                return true;
            }

            String playerUniqueId = p.getUniqueId().toString();
            String senderPlayerUniqueId = senderPlayer.getUniqueId().toString();

            PartyPlayerInvite invite = PartyPlayerInvite.findOne(senderPlayerUniqueId, playerUniqueId);

            if (invite == null) {
                p.sendMessage(senderPlayer.getName() + " have not sent you an invite to join their party yet.");
                p.sendMessage("You can ask them to enter /invite " + p.getName() + " to invite you.");
                return true;
            }

            // Invites expire in 10 minutes.
            long tenMinutesAgo = System.currentTimeMillis() - 600000;
            if (invite.getFirstCreated().before(new Timestamp(tenMinutesAgo))) {
                p.sendMessage(senderPlayer.getName() + "'s invite to their party expired. Invites expire in 10 minutes.");
                p.sendMessage("You can ask them to enter /invite " + p.getName() + " to invite you again.");
                return true;
            }

            Party newParty = Party.findParty(senderPlayerUniqueId);

            if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
                // Dismiss old party if the party leader is joining a new party.
                if (PartyPlayer.isPlayerLeader(playerUniqueId)) {
                    Party oldParty = Party.findParty(playerUniqueId);

                    if (oldParty != null) {
                        for (String oldPartyPlayerUniqueId : oldParty.getPartyPlayersUniqueId()) {
                            Player oldPartyPlayer = Bukkit.getPlayer(oldPartyPlayerUniqueId);
                            if (oldPartyPlayer != null && oldPartyPlayer.isOnline()) {
                                oldPartyPlayer.sendMessage(p.getName() + "'s party has been disbanded.");
                            }
                        }

                        oldParty.disband();
                    }
                }

                p.sendMessage("You joined " + senderPlayer.getName() + "'s party!");
                if (newParty != null) {
                    for (String partyPlayerUniqueId : newParty.getPartyPlayersUniqueId()) {
                        Player partyMemberPlayer = Bukkit.getPlayer(partyPlayerUniqueId);
                        if (partyMemberPlayer != null && partyMemberPlayer.isOnline()) {
                            partyMemberPlayer.sendMessage(p.getName() + " just joined your party!");
                        }
                    }
                } else {
                    // If this is a newly forming party, create a PartyPlayer row for the leader too.
                    Player partyMemberPlayer = Bukkit.getPlayer(senderPlayerUniqueId);
                    if (partyMemberPlayer != null && partyMemberPlayer.isOnline()) {
                        partyMemberPlayer.sendMessage(p.getName() + " just joined your party!");
                    }

                    PartyPlayer partyPlayer = PartyPlayer.findOne(senderPlayerUniqueId);
                    if (partyPlayer == null) {
                        partyPlayer = new PartyPlayer(senderPlayerUniqueId, senderPlayerUniqueId);
                        partyPlayer.save();
                    }

                }

                // Finally actually invite the player and delete the invite.
                invite.delete();

                PartyPlayer newPartyPlayer = new PartyPlayer(playerUniqueId, senderPlayerUniqueId);
                newPartyPlayer.save();

            } else {
                p.sendMessage("You can join " + senderPlayer.getName() + "'s party!");
                p.sendMessage("Enter /join " + senderPlayer.getName() + " confirm to join the party.");

                if (newParty != null) {
                    senderPlayer.sendMessage("Their party consists of: " + String.join(", ", newParty.getPartyPlayersName()));
                }

                if (PartyPlayer.isPlayerLeader(p.getUniqueId().toString())) {
                    p.sendMessage("You are currently a party leader. If you join a new party, you will disband your current party.");
                }
            }

            return true;
        }

        p.sendMessage("There are no online players named " + args[0] + ". To accept an invite both players must be online.");
        return true;
    }
}
