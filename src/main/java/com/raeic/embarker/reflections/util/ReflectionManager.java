package com.raeic.embarker.reflections.util;

import com.raeic.embarker.Globals;
import com.raeic.embarker.reflections.classes.*;

public class ReflectionManager {
    private CraftPlayerReflection craftPlayerReflection;
    private CraftWorldReflection craftWorldReflection;
    private EntityPlayerReflection entityPlayerReflection;
    private PacketPlayOutWorldBorderReflection packetPlayOutWorldBorderReflection;
    private PacketReflection packetReflection;
    private PlayerConnectionReflection playerConnectionReflection;
    private WorldBorderReflection worldBorderReflection;
    private WorldServerReflection worldServerReflection;

    public ReflectionManager() {
        init();
    }

    public CraftPlayerReflection getCraftPlayerReflection() {
        return craftPlayerReflection;
    }

    public CraftWorldReflection getCraftWorldReflection() {
        return craftWorldReflection;
    }

    public EntityPlayerReflection getEntityPlayerReflection() {
        return entityPlayerReflection;
    }

    public PacketPlayOutWorldBorderReflection getPacketPlayOutWorldBorderReflection() {
        return packetPlayOutWorldBorderReflection;
    }

    public PacketReflection getPacketReflection() {
        return packetReflection;
    }

    public WorldBorderReflection getWorldBorderReflection() {
        return worldBorderReflection;
    }

    public PlayerConnectionReflection getPlayerConnectionReflection() {
        return playerConnectionReflection;
    }

    public WorldServerReflection getWorldServerReflection() {
        return worldServerReflection;
    }

    public Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + Globals.serverVersion + "." + name);
    }

    public Class<?> getCraftBukkitClass(String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + Globals.serverVersion + "." + name);
    }

    public void init() {
        craftPlayerReflection = new CraftPlayerReflection();
        craftWorldReflection = new CraftWorldReflection();
        entityPlayerReflection = new EntityPlayerReflection();
        packetPlayOutWorldBorderReflection = new PacketPlayOutWorldBorderReflection();
        packetReflection = new PacketReflection();
        playerConnectionReflection = new PlayerConnectionReflection();
        worldBorderReflection = new WorldBorderReflection();
        worldServerReflection = new WorldServerReflection();
    }
}
