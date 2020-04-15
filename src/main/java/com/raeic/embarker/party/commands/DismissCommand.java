package com.raeic.embarker.party.commands;

import com.raeic.embarker.Globals;
import com.raeic.embarker.auth.models.EmbarkerUser;
import com.raeic.embarker.party.models.Party;
import com.raeic.embarker.party.models.PartyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DismissCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 2 || args.length < 1 || (args.length == 2 && !args[1].equalsIgnoreCase("confirm"))) {
            sender.sendMessage("Usage: /dismiss <player> (confirm)");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran by a player.");
            return true;
        }

        Player p = (Player) sender;
        String playerUniqueId = p.getUniqueId().toString();

        if (!PartyPlayer.isPlayerLeader(playerUniqueId)) {
            p.sendMessage("Sorry, only party leaders can dismiss members from the party.");
            return true;
        }

        String playerNameToDismiss = args[0];

        EmbarkerUser userToDismiss = EmbarkerUser.findOneByName(playerNameToDismiss);
        if (userToDismiss == null) {
            p.sendMessage(playerNameToDismiss + " is not a player. Did you enter their name correctly?");
            return true;
        }

        String userToDismissUniqueId = userToDismiss.getUniqueId();
        OfflinePlayer playerToDismiss = Bukkit.getOfflinePlayer(UUID.fromString(userToDismissUniqueId));
        if (!playerToDismiss.hasPlayedBefore()) {
            p.sendMessage(playerNameToDismiss + " is not a player. Did you enter their name correctly?");
            return true;
        }

        Party party = Globals.party.findParty(p.getUniqueId().toString());
        assert party != null; // If the sender player is known to be a leader, they should definitely have a party.

        if (!party.getPartyPlayersUniqueId().contains(userToDismissUniqueId)) {
            p.sendMessage(playerToDismiss.getName() + " is not a part of your party so you cannot dismiss them.");
            return true;
        }

        if (p.getName().equalsIgnoreCase(playerToDismiss.getName())) {
            p.sendMessage("You can't dismiss yourself. You can leave with /leave.");
            return true;
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
            // Notify the party that the player was dismissed
            for (String oldPartyPlayerUniqueId : party.getPartyPlayersUniqueId()) {
                Player oldPartyPlayer = Bukkit.getPlayer(UUID.fromString(oldPartyPlayerUniqueId));

                if (oldPartyPlayer != null && oldPartyPlayer.isOnline()) {
                    if (oldPartyPlayerUniqueId.equals(userToDismissUniqueId)) {
                        oldPartyPlayer.sendMessage("You were dismissed from " + p.getName() + "'s party.");
                    } else {
                        oldPartyPlayer.sendMessage(playerToDismiss.getName() + " was dismissed from " + p.getName() + "'s party.");
                    }
                }
            }

            // Disband the party of there are only two players. Otherwise just remove the dismissed player.
            if (party.getPartyPlayersUniqueId().size() <= 2) {
                party.disband();
            } else {
                PartyPlayer dismissedPartyPlayer = PartyPlayer.findOne(userToDismissUniqueId);
                if (dismissedPartyPlayer != null) {
                    dismissedPartyPlayer.delete();
                }

                // Invalidate the existing party as we updated it.
                Globals.party.invalidateCacheByKey(playerUniqueId);
            }

        } else {
            p.sendMessage("You can dismiss " + playerToDismiss.getName() + " from your party. They will no longer share land with your party.");
            p.sendMessage("Enter /dismiss " + playerToDismiss.getName() + " confirm to dismiss them.");

        }

        return true;
    }
}
