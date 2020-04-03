package com.raeic.embarker.player.models;

import com.raeic.embarker.land.models.StakedChunk;

import java.util.ArrayList;


public class EmbarkerPlayer {
    private String uniqueId;
    private ArrayList<StakedChunk> stakedChunks;

    public EmbarkerPlayer(String uniqueId) {
        this.uniqueId = uniqueId;
        this.stakedChunks = new ArrayList<>();
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public ArrayList<StakedChunk> getStakedChunks() {
        ArrayList<StakedChunk> chunksToRemove = new ArrayList<>();

        // Make sure the player owns the staked chunks
        for (StakedChunk stakedChunk : stakedChunks) {
            if (stakedChunk.isDeleted() ||!uniqueId.equals(stakedChunk.getOwnerUniqueId())) {
                chunksToRemove.add(stakedChunk);
            }
        }

        stakedChunks.removeAll(chunksToRemove);
        return stakedChunks;
    }

    public void setStakedChunks(ArrayList<StakedChunk> stakedChunks) {
        this.stakedChunks = stakedChunks;
    }
}
