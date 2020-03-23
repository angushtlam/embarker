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

public class InviteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 2 || args.length < 1 || (args.length == 2 && !args[1].equalsIgnoreCase("confirm"))) {
            sender.sendMessage("Usage: /invite <player> (confirm)");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran by a player.");
            return false;
        }

        Player p = (Player) sender;
        String playerUniqueId = p.getUniqueId().toString();

        if (!PartyPlayer.canInviteToParty(playerUniqueId)) {
            p.sendMessage("Sorry, only party leaders can invite new members to the party.");
            return true;
        }

        String playerNameToInvite = args[0];

        // Check if the player name matches with anyone online.
        List<Player> onlinePlayers = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
        for (Player playerToInvite : onlinePlayers) {
            if (!playerToInvite.getName().equalsIgnoreCase(playerNameToInvite)) {
                continue;
            }

            if (playerToInvite.getName().equalsIgnoreCase(p.getName())) {
                p.sendMessage("You can't invite yourself.");
                return true;
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
                PartyPlayerInvite invite = PartyPlayerInvite.findOne(playerUniqueId, playerToInvite.getUniqueId().toString());

                // If there are no existing invites, create a new one. Otherwise, update the timestamp.
                if (invite == null) {
                    invite = new PartyPlayerInvite(playerUniqueId, playerToInvite.getUniqueId().toString());

                    p.sendMessage("Sent an invite to " + playerToInvite.getName() + " to join your party!");
                    playerToInvite.sendMessage(p.getName() + " invited you to join their party!");

                } else {
                    invite.setFirstCreated(new Timestamp(System.currentTimeMillis()));
                    p.sendMessage("Sent an invite again to " + playerToInvite.getName() + " to join your party!");
                    playerToInvite.sendMessage(p.getName() + " invited you again to join their party! Enter /join " + p.getName() + " to join.");

                }

                Party party = Party.findParty(playerUniqueId);
                if (party != null) {
                    playerToInvite.sendMessage("Their party consists of: " + String.join(", ", party.getPartyPlayersName()));
                }

                if (PartyPlayer.isPlayerLeader(playerToInvite.getUniqueId().toString())) {
                    playerToInvite.sendMessage("You are currently a party leader. If you join a new party, you will disband your current party.");
                }

                invite.save();

            } else {
                p.sendMessage("You can invite " + playerToInvite.getName() + " to your party!");
                p.sendMessage("Enter /invite " + playerToInvite.getName() + " confirm to send your invite.");

            }

            return true;
        }

        p.sendMessage("There are no online players named " + args[0] + ". To send an invite both players must be online.");
        return true;
    }
}
