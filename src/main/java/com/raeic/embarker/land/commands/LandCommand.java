package com.raeic.embarker.land.commands;

import com.raeic.embarker.Globals;
import com.raeic.embarker.land.models.StakedChunk;
import com.raeic.embarker.party.models.Party;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LandCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran by a player.");
            return true;
        }

        Player p = (Player) sender;
        String playerUniqueId = p.getUniqueId().toString();

        int playerX = p.getLocation().getChunk().getX();
        int playerZ = p.getLocation().getChunk().getZ();
        String worldName = p.getWorld().getName();

        StringBuilder mapString = new StringBuilder();

        // If the player is a part of a party, and someone in the party owns the chunk
        Party party = Globals.party.findParty(p.getUniqueId().toString());

        for (int x = playerX - 3; x <= playerX + 3; x++) {
            for (int z = playerZ - 9; z <= playerZ + 9; z++) {
                String chunkString;

                StakedChunk stakedChunk = Globals.stakedChunks.findOne(x, z, worldName);

                if (stakedChunk == null) {
                    chunkString = "▁";
                } else if (playerUniqueId.equals(stakedChunk.getOwnerUniqueId())) {
                    chunkString = "▒";
                } else if (party != null && party.getPartyPlayersUniqueId().contains(stakedChunk.getOwnerUniqueId())) {
                    chunkString = "▚";
                } else {
                    chunkString = "█";
                }

                // Highlight where the user is with a yellow block.
                if (playerX == x && playerZ == z) {
                    chunkString = ChatColor.YELLOW + chunkString + ChatColor.WHITE;
                }

                mapString.append(chunkString);
            }
            mapString.append("\n");
        }

        p.sendMessage("Nearby land:");
        p.sendMessage(mapString.toString());
        p.sendMessage("▁ Unowned, ▒ Your land, █ Staked, ▚ Party");

        return true;
    }
}
