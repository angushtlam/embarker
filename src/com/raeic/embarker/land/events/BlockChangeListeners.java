package com.raeic.embarker.land.events;

import com.raeic.embarker.Globals;
import com.raeic.embarker.land.models.StakedChunk;
import com.raeic.embarker.party.models.Party;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import java.util.ArrayList;
import java.util.UUID;

public class BlockChangeListeners implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void handleBlockBreaks(BlockBreakEvent event) {
        Player p = event.getPlayer();
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
        Party party = Party.findParty(p.getUniqueId().toString());
        if (party != null && party.getPartyPlayersUniqueId().contains(chunkOwnerUniqueId.toString())) {
            return;
        }

        event.setCancelled(true);
        OfflinePlayer owner = Bukkit.getOfflinePlayer(chunkOwnerUniqueId);
        p.sendMessage("This chunk is staked by " + owner.getName() + ".");
    }

    @EventHandler(ignoreCancelled = true)
    public void handleBlockBurns(BlockBurnEvent event) {
        Chunk chunk = event.getBlock().getChunk();
        StakedChunk stakedChunk = Globals.stakedChunks.findOne(
                chunk.getX(),
                chunk.getZ(),
                chunk.getWorld().getName()
        );

        // Blocks in staked chunks should not burn
        if (stakedChunk != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handleBlockCanBuild(BlockCanBuildEvent event) {
        Player p = event.getPlayer();
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

        // Player can be null here apparently. The non players cannot modify the chunk either.
        if (p == null) {
            event.setBuildable(false);
            return;
        }

        UUID chunkOwnerUniqueId = UUID.fromString(stakedChunk.getOwnerUniqueId());

        // If the player owns the chunk they can modify it.
        if (p.getUniqueId().equals(chunkOwnerUniqueId)) {
            return;
        }

        // If the player is a part of a party, and someone in the party owns the chunk
        Party party = Party.findParty(p.getUniqueId().toString());
        if (party != null && party.getPartyPlayersUniqueId().contains(chunkOwnerUniqueId.toString())) {
            return;
        }

        event.setBuildable(false);

        OfflinePlayer owner = Bukkit.getOfflinePlayer(chunkOwnerUniqueId);
        p.sendMessage("This chunk is staked by " + owner.getName() + ".");
    }

    @EventHandler(ignoreCancelled = true)
    public void handleBlockDamages(BlockDamageEvent event) {
        Player p = event.getPlayer();
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
        Party party = Party.findParty(p.getUniqueId().toString());
        if (party != null && party.getPartyPlayersUniqueId().contains(chunkOwnerUniqueId.toString())) {
            return;
        }

        event.setCancelled(true);

        OfflinePlayer owner = Bukkit.getOfflinePlayer(chunkOwnerUniqueId);
        p.sendMessage("This chunk is staked by " + owner.getName() + ".");
    }

    @EventHandler(ignoreCancelled = true)
    public void handleBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
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
        Party party = Party.findParty(p.getUniqueId().toString());
        if (party != null && party.getPartyPlayersUniqueId().contains(chunkOwnerUniqueId.toString())) {
            return;
        }

        event.setBuild(false);
        event.setCancelled(true);

        OfflinePlayer owner = Bukkit.getOfflinePlayer(chunkOwnerUniqueId);
        p.sendMessage("This chunk is staked by " + owner.getName() + ".");
    }

    @EventHandler(ignoreCancelled = true)
    public void handleFireSpread(BlockSpreadEvent event) {
        // Disable fire spreading only within staked chunks.
        if (!event.getNewState().getType().equals(Material.FIRE)) {
            return;
        }

        Chunk chunk = event.getBlock().getChunk();
        StakedChunk stakedChunk = Globals.stakedChunks.findOne(
                chunk.getX(),
                chunk.getZ(),
                chunk.getWorld().getName()
        );

        // Blocks in staked chunks should not burn
        if (stakedChunk != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handleSignChange(SignChangeEvent event) {
        Player p = event.getPlayer();
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
        Party party = Party.findParty(p.getUniqueId().toString());
        if (party != null && party.getPartyPlayersUniqueId().contains(chunkOwnerUniqueId.toString())) {
            return;
        }

        event.setCancelled(true);

        OfflinePlayer owner = Bukkit.getOfflinePlayer(chunkOwnerUniqueId);
        p.sendMessage("This chunk is staked by " + owner.getName() + ".");
    }

    @EventHandler(ignoreCancelled = true)
    public void handleBlockMultiPlace(BlockMultiPlaceEvent event) {
        Player p = event.getPlayer();

        Chunk chunkAdditionalBlockPlaced = event.getBlockPlaced().getChunk();
        Chunk chunkFirstBlockPlaced = event.getBlock().getChunk();

        ArrayList<StakedChunk> chunksToCheck = new ArrayList<>();

        if (chunkFirstBlockPlaced.getX() == chunkAdditionalBlockPlaced.getX() &&
            chunkFirstBlockPlaced.getZ() == chunkAdditionalBlockPlaced.getZ()) {
            StakedChunk stakedChunk = Globals.stakedChunks.findOne(
                    chunkFirstBlockPlaced.getX(),
                    chunkFirstBlockPlaced.getZ(),
                    chunkFirstBlockPlaced.getWorld().getName()
            );

            // Ignore if the chunk is not staked.
            if (stakedChunk != null) {
                chunksToCheck.add(stakedChunk);
            }
        } else {
            StakedChunk stakedChunk = Globals.stakedChunks.findOne(
                    chunkAdditionalBlockPlaced.getX(),
                    chunkAdditionalBlockPlaced.getZ(),
                    chunkAdditionalBlockPlaced.getWorld().getName()
            );

            // Ignore if the chunk is not staked.
            if (stakedChunk != null) {
                chunksToCheck.add(stakedChunk);
            }
        }

        for (StakedChunk stakedChunk : chunksToCheck) {
            UUID chunkOwnerUniqueId = UUID.fromString(stakedChunk.getOwnerUniqueId());

            // If the player owns the chunk they can modify it.
            if (p.getUniqueId().equals(chunkOwnerUniqueId)) {
                return;
            }

            // If the player is a part of a party, and someone in the party owns the chunk
            Party party = Party.findParty(p.getUniqueId().toString());
            if (party != null && party.getPartyPlayersUniqueId().contains(chunkOwnerUniqueId.toString())) {
                return;
            }

            event.setBuild(false);
            event.setCancelled(true);

            OfflinePlayer owner = Bukkit.getOfflinePlayer(chunkOwnerUniqueId);
            p.sendMessage("This chunk is staked by " + owner.getName() + ".");

            return;
        }
    }
}