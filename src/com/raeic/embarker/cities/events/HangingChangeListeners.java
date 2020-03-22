package com.raeic.embarker.cities.events;

import com.raeic.embarker.cities.models.StakedChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

import java.util.UUID;

public class HangingChangeListeners implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void handleHangingBreak(HangingBreakByEntityEvent event) {
        Entity remover = event.getRemover();

        if (!(remover instanceof Player)) {
            return;
        }

        Player p = (Player) remover;
        Hanging hanging = event.getEntity();
        Chunk chunk = hanging.getLocation().getChunk();

        StakedChunk stakedChunk = StakedChunk.findOne(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());

        // Ignore if the chunk is not staked.
        if (stakedChunk == null) {
            return;
        }

        UUID chunkOwnerUniqueId = UUID.fromString(stakedChunk.getOwnerUniqueId());

        // If the player does not have access to the chunk, they cannot modify it.
        if (!p.getUniqueId().equals(chunkOwnerUniqueId)) {
            event.setCancelled(true);

            OfflinePlayer owner = Bukkit.getOfflinePlayer(chunkOwnerUniqueId);
            p.sendMessage("This chunk is staked by " + owner.getName() + ".");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handleHangingPlace(HangingPlaceEvent event) {
        Player p = event.getPlayer();

        if (p == null) {
            return;
        }

        Chunk chunk = event.getBlock().getChunk();
        StakedChunk stakedChunk = StakedChunk.findOne(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());

        // Ignore if the chunk is not staked.
        if (stakedChunk == null) {
            return;
        }

        UUID chunkOwnerUniqueId = UUID.fromString(stakedChunk.getOwnerUniqueId());

        // If the player does not have access to the chunk, they cannot modify it.
        if (!p.getUniqueId().equals(chunkOwnerUniqueId)) {
            event.setCancelled(true);

            OfflinePlayer owner = Bukkit.getOfflinePlayer(chunkOwnerUniqueId);
            p.sendMessage("This chunk is staked by " + owner.getName() + ".");
        }
    }
}
