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

public class StakeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran by a player.");
            return false;
        }

        Player p = (Player) sender;
        Chunk chunk = p.getLocation().getChunk();

        StakedChunk stakedChunk = StakedChunk.findOne(chunk.getX(), chunk.getZ());

        if (stakedChunk != null) {
            UUID ownerUniqueId = UUID.fromString(stakedChunk.getOwnerUniqueId());
            OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUniqueId);

            if (p.getUniqueId().equals(ownerUniqueId)) {
                p.sendMessage("You have already staked this chunk of land!");
            } else if (owner.getName() != null) {
                p.sendMessage("Sorry, this chunk of land is already staked by " + owner.getName() + ".");
            } else {
                // Should not happen, but just in case there's missing data.
                p.sendMessage("Sorry, this chunk of land is already staked by an unknown player.");
            }
        } else {
            StakedChunk newStakedChunk = new StakedChunk(chunk.getX(), chunk.getZ(), p.getUniqueId().toString());
            newStakedChunk.save();

            p.sendMessage("You staked this chunk!");
        }

        return true;
    }
}
