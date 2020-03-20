package com.raeic.embarker.cities.commands;

import com.raeic.embarker.cities.models.StakedChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnstakeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran by a player.");
            return false;
        }

        Player p = (Player) sender;
        Chunk chunk = p.getLocation().getChunk();

        StakedChunk stakedChunk = StakedChunk.findOne(chunk.getX(), chunk.getZ());

        if (stakedChunk == null) {
            p.sendMessage("This chunk of land is not staked.");
        } else if (stakedChunk.getOwnerUniqueId().equals(p.getUniqueId().toString())){
            stakedChunk.delete();
            p.sendMessage("You have unstaked this chunk of land.");
        } else {
            OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(stakedChunk.getOwnerUniqueId()));

            if (owner.getName() != null) {
                p.sendMessage("Sorry, only the owner " + owner.getName() + " can unstake this chunk of land.");
            } else {
                // Should not happen, but just in case there's missing data.
                p.sendMessage("Sorry, only the owner can unstake this chunk of land.");
            }
        }

        return true;
    }
}
