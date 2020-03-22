package com.raeic.embarker.land.events;

import com.raeic.embarker.land.models.StakedChunk;
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
    public void handleBlockBurns(BlockBurnEvent event) {
        Chunk chunk = event.getBlock().getChunk();
        StakedChunk stakedChunk = StakedChunk.findOne(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());

        // Blocks in staked chunks should not burn
        if (stakedChunk != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handleBlockCanBuild(BlockCanBuildEvent event) {
        Player p = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();

        StakedChunk stakedChunk = StakedChunk.findOne(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());

        // Ignore if the chunk is not staked.
        if (stakedChunk == null) {
            return;
        }

        // If the editing player doesn't exist, then it should not be editable.
        if (p == null) {
            event.setBuildable(false);
            return;
        }

        UUID chunkOwnerUniqueId = UUID.fromString(stakedChunk.getOwnerUniqueId());

        // If the player does not have access to the chunk, they cannot modify it.
        if (!p.getUniqueId().equals(chunkOwnerUniqueId)) {
            event.setBuildable(false);

            OfflinePlayer owner = Bukkit.getOfflinePlayer(chunkOwnerUniqueId);
            p.sendMessage("This chunk is staked by " + owner.getName() + ".");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handleBlockDamages(BlockDamageEvent event) {
        Player p = event.getPlayer();
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

    @EventHandler(ignoreCancelled = true)
    public void handleBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();

        StakedChunk stakedChunk = StakedChunk.findOne(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());

        // Ignore if the chunk is not staked.
        if (stakedChunk == null) {
            return;
        }

        UUID chunkOwnerUniqueId = UUID.fromString(stakedChunk.getOwnerUniqueId());

        // If the player does not have access to the chunk, they cannot modify it.
        if (!p.getUniqueId().equals(chunkOwnerUniqueId)) {
            event.setBuild(false);
            event.setCancelled(true);

            OfflinePlayer owner = Bukkit.getOfflinePlayer(chunkOwnerUniqueId);
            p.sendMessage("This chunk is staked by " + owner.getName() + ".");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handleFireSpread(BlockSpreadEvent event) {
        // Disable fire spreading only within staked chunks.
        if (!event.getNewState().getType().equals(Material.FIRE)) {
            return;
        }

        Chunk chunk = event.getBlock().getChunk();
        StakedChunk stakedChunk = StakedChunk.findOne(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());

        // Blocks in staked chunks should not burn
        if (stakedChunk != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handleSignChange(SignChangeEvent event) {
        Player p = event.getPlayer();
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

    @EventHandler(ignoreCancelled = true)
    public void handleBlockMultiPlace(BlockMultiPlaceEvent event) {
        Player p = event.getPlayer();

        Chunk chunkAdditionalBlockPlaced = event.getBlockPlaced().getChunk();
        Chunk chunkFirstBlockPlaced = event.getBlock().getChunk();

        ArrayList<StakedChunk> chunksToCheck = new ArrayList<>();

        if (chunkFirstBlockPlaced.getX() == chunkAdditionalBlockPlaced.getX() &&
            chunkFirstBlockPlaced.getZ() == chunkAdditionalBlockPlaced.getZ()) {
            StakedChunk stakedChunk = StakedChunk.findOne(chunkFirstBlockPlaced.getX(), chunkFirstBlockPlaced.getZ(), chunkFirstBlockPlaced.getWorld().getName());

            // Ignore if the chunk is not staked.
            if (stakedChunk != null) {
                chunksToCheck.add(stakedChunk);
            }
        } else {
            StakedChunk stakedChunk = StakedChunk.findOne(chunkAdditionalBlockPlaced.getX(), chunkAdditionalBlockPlaced.getZ(), chunkAdditionalBlockPlaced.getWorld().getName());

            // Ignore if the chunk is not staked.
            if (stakedChunk != null) {
                chunksToCheck.add(stakedChunk);
            }
        }

        for (StakedChunk stakedChunk : chunksToCheck) {
            UUID chunkOwnerUniqueId = UUID.fromString(stakedChunk.getOwnerUniqueId());

            // If the player does not have access to the chunk, they cannot modify it.
            if (!p.getUniqueId().equals(chunkOwnerUniqueId)) {
                event.setBuild(false);
                event.setCancelled(true);

                OfflinePlayer owner = Bukkit.getOfflinePlayer(chunkOwnerUniqueId);
                p.sendMessage("This chunk is staked by " + owner.getName() + ".");

                // End early as one cancel here is enough.
                return;
            }
        }
    }
}