package com.raeic.embarker.land.commands;

import com.raeic.embarker.Globals;
import com.raeic.embarker.land.enums.UnstakeCondition;
import com.raeic.embarker.land.models.StakedChunk;
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
        if (args.length > 1 || (args.length == 1 && !args[0].equalsIgnoreCase("confirm"))) {
            sender.sendMessage("Usage: /unstake (confirm)");
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran by a player.");
            return false;
        }

        Player p = (Player) sender;
        String ownerUniqueId = p.getUniqueId().toString();

        Chunk chunk = p.getLocation().getChunk();

        StakedChunk stakedChunk = Globals.stakedChunks.findOne(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());

        if (stakedChunk == null) {
            p.sendMessage("This chunk of land is not staked.");

        } else if (stakedChunk.getOwnerUniqueId().equals(ownerUniqueId)) {
            UnstakeCondition unstakeCondition = Globals.stakedChunks.canUnstake(
                    ownerUniqueId,
                    chunk.getX(),
                    chunk.getZ(),
                    chunk.getWorld().getName()
            );

            if (unstakeCondition.equals(UnstakeCondition.CAN_UNSTAKE)) {
                Globals.landScheduler.showPlayerChunkBorder(p);

                if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
                    stakedChunk.delete();
                    p.sendMessage("You have unstaked this chunk of land.");
                } else {
                    p.sendMessage("This chunk of land can be unstaked!");
                    p.sendMessage("You can unstake this chunk of land by entering /unstake confirm.");
                    p.sendMessage("Note that you will not receive your Emeralds back.");
                }
            } else if (unstakeCondition.equals(UnstakeCondition.NO_ADJACENT)) {
                p.sendMessage("You cannot unstake here because your staked chunks of land need to be contiguous.");
            } else {
                p.sendMessage("You cannot unstake here.");
            }
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
