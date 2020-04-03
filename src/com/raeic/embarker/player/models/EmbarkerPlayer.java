package com.raeic.embarker.player.models;

import com.raeic.embarker.land.models.StakedChunk;

import java.util.HashSet;


public class EmbarkerPlayer {
    private String uniqueId;
    private HashSet<StakedChunk> stakedChunks;

    public EmbarkerPlayer(String uniqueId) {
        this.uniqueId = uniqueId;
        this.stakedChunks = new HashSet<>();
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public HashSet<StakedChunk> getStakedChunks() {
        return parseStakedChunks(stakedChunks);
    }

    public void setStakedChunks(HashSet<StakedChunk> stakedChunks) {
        this.stakedChunks = parseStakedChunks(stakedChunks);
    }

    private HashSet<StakedChunk> parseStakedChunks(HashSet<StakedChunk> stakedChunks) {
        HashSet<StakedChunk> parsedStakedChunks = new HashSet<>();

        // Make sure the player owns the staked chunks
        for (StakedChunk stakedChunk : stakedChunks) {
            if (!stakedChunk.isDeleted() && uniqueId.equals(stakedChunk.getOwnerUniqueId())) {
                parsedStakedChunks.add(stakedChunk);
            }
        }

        return parsedStakedChunks;
    }
}
