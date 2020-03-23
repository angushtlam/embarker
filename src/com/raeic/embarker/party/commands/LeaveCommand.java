package com.raeic.embarker.party.commands;

import com.raeic.embarker.party.models.Party;
import com.raeic.embarker.party.models.PartyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class LeaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1 || (args.length == 1 && !args[0].equalsIgnoreCase("confirm"))) {
            sender.sendMessage("Usage: /leave (confirm)");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran by a player.");
            return true;
        }

        Player p = (Player) sender;
        String playerUniqueId = p.getUniqueId().toString();

        Party party = Party.findParty(p.getUniqueId().toString());
        if (party == null) {
            p.sendMessage("There is no party to leave.");
            return true;
        }

        boolean isPartyLeader = party.getLeaderUniqueId().equals(playerUniqueId);

        if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
            String newLeaderName = null;
            String newLeaderUniqueId = null;

            ArrayList<String> oldPartyPlayersUniqueId = party.getPartyPlayersUniqueId();

            // If the leader is leaving, reassign the players to next leader.
            if (oldPartyPlayersUniqueId.size() > 1 && isPartyLeader) {
                for (String nextLeaderUniqueId : oldPartyPlayersUniqueId) {
                    if (nextLeaderUniqueId.equals(playerUniqueId)) {
                        continue;
                    }

                    newLeaderName = Bukkit.getOfflinePlayer(UUID.fromString(nextLeaderUniqueId)).getName();
                    newLeaderUniqueId = nextLeaderUniqueId;
                    break;
                }
            }

            // Send the party a leaving message.
            for (String oldPartyPlayerUniqueId : oldPartyPlayersUniqueId) {
                if (oldPartyPlayerUniqueId.equals(playerUniqueId)) {
                    continue;
                }

                Player oldPartyPlayer = Bukkit.getPlayer(UUID.fromString(oldPartyPlayerUniqueId));
                if (oldPartyPlayer != null && oldPartyPlayer.isOnline()) {
                    if (newLeaderName != null) {
                        oldPartyPlayer.sendMessage("Your party leader " + p.getName() + " left the party. " +
                                                   "The new leader is " + newLeaderName + ".");
                    } else {
                        oldPartyPlayer.sendMessage(p.getName() + " left your party.");
                    }
                }
            }

            // If there is a new leader, swap the
            if (newLeaderUniqueId != null) {
                PartyPlayer.changeLeader(playerUniqueId, newLeaderUniqueId);
            }

            // Disband the party of there are only two players. Otherwise just remove the leaving player.
            if (party.getPartyPlayersUniqueId().size() <= 2) {
                party.disband();
            } else {
                PartyPlayer leavingPartyPlayer = PartyPlayer.findOne(playerUniqueId);
                if (leavingPartyPlayer != null) {
                    leavingPartyPlayer.delete();
                }
            }

            p.sendMessage("You left your party.");

        } else {
            p.sendMessage("You can leave your party. Players will no longer share land. " +
                          "Enter /leave confirm to leave the party.");

            if (party.getLeaderUniqueId().equals(p.getUniqueId().toString())) {
                p.sendMessage("You are the leader of your party. If you leave it, a random player " +
                              "remaining in your party will become the new leader.");
            }
        }

        return true;

    }
}
