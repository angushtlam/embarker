package com.raeic.embarker.land.events;

import com.raeic.embarker.land.models.StakedChunk;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityBlockChangeListeners implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void handleEntityExplode(EntityExplodeEvent event) {
        // Prevent entity explosion from destroying blocks within staked chunks.
        List<Block> blockList = event.blockList();

        // If chunk is owned, it stores true.
        HashMap<String, Boolean> chunksOwnership = new HashMap<>();
        ArrayList<Block> finalBlockList = new ArrayList<>();

        for (Block block : blockList) {
            Chunk chunk = block.getChunk();
            int coordX = chunk.getX();
            int coordZ = chunk.getZ();
            String key = coordX + ":" + coordZ;

            // If the hashmap already contains the chunk, don't fetch the chunk.
            if (chunksOwnership.containsKey(key)) {
                boolean isChunkOwned = chunksOwnership.get(key);

                if (!isChunkOwned) {
                    finalBlockList.add(block);
                }

            } else {
                StakedChunk stakedChunk = StakedChunk.findOne(coordX, coordZ, chunk.getWorld().getName());
                if (stakedChunk != null) {
                    chunksOwnership.put(key, true);
                } else {
                    chunksOwnership.put(key, false);
                    finalBlockList.add(block);
                }
            }
        }

        // Only include the blocks in unstaked chunks to the explosion list.
        blockList.clear();
        blockList.addAll(finalBlockList);
    }
}
