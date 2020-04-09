package com.raeic.embarker.player.models;

import com.raeic.embarker.land.models.StakedChunk;

import java.util.HashSet;

public class EmbarkerPlayer {
    private String uniqueId;
    private HashSet<StakedChunk> stakedChunks;

    public EmbarkerPlayer(String uniqueId, HashSet<StakedChunk> stakedChunks) {
        this.uniqueId = uniqueId;
        this.stakedChunks = stakedChunks;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public StakedChunk[] getStakedChunks() {
        return this.stakedChunks.toArray(new StakedChunk[0]);
    }
}
