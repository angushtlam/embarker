package com.raeic.embarker.party.commands;

import com.raeic.embarker.Globals;
import com.raeic.embarker.party.models.Party;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PartyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran by a player.");
            return true;
        }

        Player p = (Player) sender;
        String playerUniqueId = p.getUniqueId().toString();
        Party party = Globals.party.findParty(playerUniqueId);

        // Players can be in their own party.
        if (party == null || party.getPartyPlayersUniqueId().size() == 1) {
            p.sendMessage("You do not have a party. Invite people with /invite <player>!");
            p.sendMessage("Party members share each other's staked lands.");
            return true;
        }

        if (party.getLeaderUniqueId().equals(playerUniqueId)) {
            p.sendMessage("You are the leader of your party.");
            p.sendMessage("You can leave your party with /leave, " +
                          "disband it with /disband, " +
                          "invite a new player with /invite <player>, " +
                          "and dismiss another player with /dismiss <player>.");
        } else {
            OfflinePlayer leaderPlayer = Bukkit.getOfflinePlayer(UUID.fromString(party.getLeaderUniqueId()));
            p.sendMessage("You are in " + leaderPlayer.getName() + "'s party.");
            p.sendMessage("You can leave your party with /leave.");
        }

        p.sendMessage("Your party consists of: " + String.join(", ", party.getPartyPlayersName()));
        return true;
    }
}
