package com.raeic.embarker.land.events;

import com.raeic.embarker.Globals;
import com.raeic.embarker.land.models.StakedChunk;
import com.raeic.embarker.party.models.Party;
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

        StakedChunk stakedChunk = Globals.stakedChunks.findOne(
                chunk.getX(),
                chunk.getZ(),
                chunk.getWorld().getName()
        );

        // Ignore if the chunk is not staked.
        if (stakedChunk == null) {
            return;
        }

        UUID chunkOwnerUniqueId = UUID.fromString(stakedChunk.getOwnerUniqueId());

        // If the player owns the chunk they can modify it.
        if (p.getUniqueId().equals(chunkOwnerUniqueId)) {
            return;
        }

        // If the player is a part of a party, and someone in the party owns the chunk
        Party party = Globals.party.findParty(p.getUniqueId().toString());
        if (party != null && party.getPartyPlayersUniqueId().contains(chunkOwnerUniqueId.toString())) {
            return;
        }

        event.setCancelled(true);

        OfflinePlayer owner = Bukkit.getOfflinePlayer(chunkOwnerUniqueId);
        p.sendMessage("This chunk is staked by " + owner.getName() + ".");
    }

    @EventHandler(ignoreCancelled = true)
    public void handleHangingPlace(HangingPlaceEvent event) {
        Player p = event.getPlayer();

        if (p == null) {
            return;
        }

        Chunk chunk = event.getBlock().getChunk();
        StakedChunk stakedChunk = Globals.stakedChunks.findOne(
                chunk.getX(),
                chunk.getZ(),
                chunk.getWorld().getName()
        );

        // Ignore if the chunk is not staked.
        if (stakedChunk == null) {
            return;
        }

        UUID chunkOwnerUniqueId = UUID.fromString(stakedChunk.getOwnerUniqueId());

        // If the player owns the chunk they can modify it.
        if (p.getUniqueId().equals(chunkOwnerUniqueId)) {
            return;
        }

        // If the player is a part of a party, and someone in the party owns the chunk
        Party party = Globals.party.findParty(p.getUniqueId().toString());
        if (party != null && party.getPartyPlayersUniqueId().contains(chunkOwnerUniqueId.toString())) {
            return;
        }

        event.setCancelled(true);

        OfflinePlayer owner = Bukkit.getOfflinePlayer(chunkOwnerUniqueId);
        p.sendMessage("This chunk is staked by " + owner.getName() + ".");
    }
}
