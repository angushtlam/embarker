package com.raeic.embarker.land.events;

import java.util.UUID;

import com.destroystokyo.paper.Title;
import com.raeic.embarker.Globals;
import com.raeic.embarker.land.models.StakedChunk;
import com.raeic.embarker.party.models.Party;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChestProtectionListeners implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void handlePlayerInteractChest(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Material blockMaterial = block.getType();
        
        if (!(
            blockMaterial.equals(Material.CHEST) || 
            blockMaterial.equals(Material.SHULKER_BOX) || 
            blockMaterial.equals(Material.BARREL)
            )) {
            return;
        }
        
        Player p = event.getPlayer();
        Chunk chunk = block.getLocation().getChunk();

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
        p.sendTitle(new Title("✖", "Staked by " + owner.getName(), 0, 10, 1));
    }
}
