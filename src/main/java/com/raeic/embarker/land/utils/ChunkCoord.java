package com.raeic.embarker.land.utils;

public class ChunkCoord {
    public int coordX;
    public int coordZ;

    public ChunkCoord(int coordX, int coordZ) {
        this.coordX = coordX;
        this.coordZ = coordZ;
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordZ() {
        return coordZ;
    }
}
