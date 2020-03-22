package com.raeic.embarker.cities.events;

import com.raeic.embarker.cities.models.StakedChunk;
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
            int coordX = block.getChunk().getX();
            int coordZ = block.getChunk().getZ();
            String key = coordX + ":" + coordZ;

            // If the hashmap already contains the chunk, don't fetch the chunk.
            if (chunksOwnership.containsKey(key)) {
                boolean isChunkOwned = chunksOwnership.get(key);

                if (!isChunkOwned) {
                    finalBlockList.add(block);
                }

            } else {
                StakedChunk stakedChunk = StakedChunk.findOne(coordX, coordZ);
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
