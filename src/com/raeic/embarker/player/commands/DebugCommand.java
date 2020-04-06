package com.raeic.embarker.player.commands;

import com.raeic.embarker.Globals;
import com.raeic.embarker.land.models.StakedChunk;
import com.raeic.embarker.player.models.EmbarkerPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran by a player.");
            return true;
        }

        Player p = (Player) sender;
        String playerUniqueId = p.getUniqueId().toString();

        EmbarkerPlayer embarkerPlayer = Globals.embarkerPlayers.findOne(playerUniqueId);

        if (embarkerPlayer == null) {
            p.sendMessage("An error occurred. Please try again later.");
            return true;
        }

        StakedChunk[] chunks = embarkerPlayer.getStakedChunks();

        for (int i = 0; i < chunks.length; i++) {
            StakedChunk chunk = chunks[i];
            p.sendMessage(i + ": (" + chunk.getCoordX() + ", " + chunk.getCoordZ() + ") ");
        }

        return true;
    }
}
