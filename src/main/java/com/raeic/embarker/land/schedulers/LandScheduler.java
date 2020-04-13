package com.raeic.embarker.land.schedulers;

import com.raeic.embarker.Globals;
import com.raeic.embarker.land.utils.ChunkCoord;
import com.raeic.embarker.reflections.classes.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LandScheduler {
    private static final long PLAYER_VIEWING_BORDER_SCHEDULER_TICKS = 10L;

    private ConcurrentHashMap<Player, PlayerViewingBorderMetadata> playerViewingBorderMap;

    public LandScheduler() {
        init();
    }

    public void showPlayerChunkBorder(Player p) {
        // Only show the border if it is initialized.
        if (playerViewingBorderMap == null) {
            return;
        }

        Chunk chunk = p.getLocation().getChunk();
        playerViewingBorderMap.put(p, new PlayerViewingBorderMetadata(
                new ChunkCoord(chunk.getX(), chunk.getZ()),
                5 * 20L // 5 seconds
        ));
    }

    public void init() {
        // Set up reflection for classes needed
        CraftPlayerReflection craftPlayerReflection = Globals.reflectionManager.getCraftPlayerReflection();
        CraftWorldReflection craftWorldReflection = Globals.reflectionManager.getCraftWorldReflection();
        EntityPlayerReflection entityPlayerReflection = Globals.reflectionManager.getEntityPlayerReflection();
        PacketPlayOutWorldBorderReflection packetPlayOutWorldBorderReflection = Globals.reflectionManager.getPacketPlayOutWorldBorderReflection();
        PacketReflection packetReflection = Globals.reflectionManager.getPacketReflection();
        PlayerConnectionReflection playerConnectionReflection = Globals.reflectionManager.getPlayerConnectionReflection();
        WorldBorderReflection worldBorderReflection = Globals.reflectionManager.getWorldBorderReflection();
        WorldServerReflection worldServerReflection = Globals.reflectionManager.getWorldServerReflection();

        // Make sure all the reflected classes are available.
        if (!(craftPlayerReflection.isReflectionReady() &&
              craftWorldReflection.isReflectionReady() &&
              entityPlayerReflection.isReflectionReady() &&
              packetPlayOutWorldBorderReflection.isReflectionReady() &&
              packetReflection.isReflectionReady() &&
              playerConnectionReflection.isReflectionReady() &&
              worldBorderReflection.isReflectionReady() &&
              worldServerReflection.isReflectionReady())) {
            System.out.println("[Embarker] Reflections are not working for LandScheduler. Visual staking borders are disabled.");
            return;
        }

        playerViewingBorderMap = new ConcurrentHashMap<>();

        Class<?> CraftPlayer = craftPlayerReflection.getReflectedClass();
        Method CraftPlayer_getHandle = craftPlayerReflection.getHandleMethod();

        Class<?> CraftWorld = craftWorldReflection.getReflectedClass();
        Method CraftWorld_getHandle = craftWorldReflection.getHandleMethod();

        Class<?> EntityPlayer = entityPlayerReflection.getReflectedClass();
        Field EntityPlayer_playerConnection = entityPlayerReflection.getPlayerConnectionField();

        Object EnumWorldBorderAction_INITIALIZE = packetPlayOutWorldBorderReflection.getEnumWorldBorderActionEnum("INITIALIZE");
        Constructor<?> PacketPlayOutWorldBorderConstructor = packetPlayOutWorldBorderReflection.getConstructor();

        Class<?> Packet = packetReflection.getReflectedClass();

        Class<?> PlayerConnection = playerConnectionReflection.getReflectedClass();
        Method PlayerConnection_sendPacket = playerConnectionReflection.sendPacketMethod();

        Constructor<?> WorldBorderConstructor = worldBorderReflection.getConstructor();
        Method WorldBorder_setSize = worldBorderReflection.setSizeMethod();
        Method WorldBorder_setCenter = worldBorderReflection.setCenterMethod();
        Field WorldBorder_world = worldBorderReflection.getWorldField();

        Class<?> WorldServer = worldServerReflection.getReflectedClass();

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Globals.plugin, () -> {
            ArrayList<Player> playersToRemove = new ArrayList<>();

            for (Map.Entry<Player, PlayerViewingBorderMetadata> entry : playerViewingBorderMap.entrySet()) {
                Player p = entry.getKey();
                Chunk chunk = p.getLocation().getChunk();

                PlayerViewingBorderMetadata metadata = entry.getValue();

                try {
                    // Begin building a world border
                    Object craftPlayer = CraftPlayer.cast(p);
                    Object worldBorder = WorldBorderConstructor.newInstance();


                    Object worldServer = CraftWorld_getHandle.invoke(CraftWorld.cast(p.getWorld()));
                    WorldBorder_world.set(worldBorder, WorldServer.cast(worldServer));

                    boolean playerIsInChunk = chunk.getX() == metadata.getChunk().getCoordX() &&
                                              chunk.getZ() == metadata.getChunk().getCoordZ();

                    if (metadata.getDuration() > 0 && playerIsInChunk) {
                        WorldBorder_setSize.invoke(worldBorder, 17);
                        WorldBorder_setCenter.invoke(worldBorder, (chunk.getX() * 16) + 8, (chunk.getZ() * 16) + 8);

                        metadata.setDuration(metadata.getDuration() - PLAYER_VIEWING_BORDER_SCHEDULER_TICKS);
                    } else {
                        // Set border to 599999968 to disable it
                        // https://minecraft.gamepedia.com/World_border#Commands
                        WorldBorder_setSize.invoke(worldBorder, 59999968);
                        WorldBorder_setCenter.invoke(worldBorder, 0, 0);

                        playersToRemove.add(p);
                    }

                    Object packet = PacketPlayOutWorldBorderConstructor.newInstance(
                            worldBorder,
                            EnumWorldBorderAction_INITIALIZE
                    );

                    Object entityPlayer = EntityPlayer.cast(CraftPlayer_getHandle.invoke(craftPlayer));
                    Object playerConnection = PlayerConnection.cast(EntityPlayer_playerConnection.get(entityPlayer));
                    PlayerConnection_sendPacket.invoke(playerConnection, Packet.cast(packet));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            // Clean up the map of players that already removed the border.
            for (Player p : playersToRemove) {
                playerViewingBorderMap.remove(p);
            }
        }, 0L, PLAYER_VIEWING_BORDER_SCHEDULER_TICKS);
    }

    private static class PlayerViewingBorderMetadata {
        private ChunkCoord chunk;
        private long duration;

        public PlayerViewingBorderMetadata(ChunkCoord chunk, long duration) {
            this.chunk = chunk;
            this.duration = duration;
        }

        public ChunkCoord getChunk() {
            return chunk;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }
}
