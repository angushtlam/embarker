package com.raeic.embarker.party.commands;

import com.raeic.embarker.Globals;
import com.raeic.embarker.party.models.Party;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DisbandCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1 || (args.length == 1 && !args[0].equalsIgnoreCase("confirm"))) {
            sender.sendMessage("Usage: /disband (confirm)");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran by a player.");
            return true;
        }

        Player p = (Player) sender;

        Party party = Globals.party.findParty(p.getUniqueId().toString());

        if (party == null) {
            p.sendMessage("There is no party to disband.");
            return true;
        }

        if (!party.getLeaderUniqueId().equals(p.getUniqueId().toString())) {
            p.sendMessage("You are not the leader of the party so you cannot disband the party.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
            for (String oldPartyPlayerUniqueId : party.getPartyPlayersUniqueId()) {
                Player oldPartyPlayer = Bukkit.getPlayer(UUID.fromString(oldPartyPlayerUniqueId));
                if (oldPartyPlayer != null && oldPartyPlayer.isOnline()) {
                    oldPartyPlayer.sendMessage(p.getName() + "'s party has been disbanded.");
                }
            }

            party.disband();

        } else {
            p.sendMessage("You can disband your party. Players will no longer share land.");
            p.sendMessage("Enter /disband confirm to disband the party.");
        }

        return true;

    }
}
