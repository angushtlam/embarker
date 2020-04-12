package com.raeic.embarker.land.schedulers;

import com.raeic.embarker.Globals;
import com.raeic.embarker.land.utils.ChunkCoord;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

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
        playerViewingBorderMap = new ConcurrentHashMap<>();
        init();
    }

    public void showPlayerChunkBorder(Player p) {
        Chunk chunk = p.getLocation().getChunk();

        playerViewingBorderMap.put(p, new PlayerViewingBorderMetadata(
                new ChunkCoord(chunk.getX(), chunk.getZ()),
                5 * 20L // 5 seconds
        ));
    }

    public void init() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(Globals.plugin, () -> {
            ArrayList<Player> playersToRemove = new ArrayList<>();

            for (Map.Entry<Player, PlayerViewingBorderMetadata> entry : playerViewingBorderMap.entrySet()) {
                Player p = entry.getKey();
                Chunk chunk = p.getLocation().getChunk();

                PlayerViewingBorderMetadata metadata = entry.getValue();

                try {
                    // Set up reflection for classes needed
                    Class<?> CraftPlayer = Globals.reflectionUtil.getCraftBukkitClass("entity.CraftPlayer");
                    Method CraftPlayer_getHandle = CraftPlayer.getMethod("getHandle");

                    Class<?> CraftWorld = Globals.reflectionUtil.getCraftBukkitClass("CraftWorld");
                    Method CraftWorld_getHandle = CraftWorld.getMethod("getHandle");

                    Class<?> WorldServer = Globals.reflectionUtil.getNMSClass("WorldServer");

                    Class<?> WorldBorder = Globals.reflectionUtil.getNMSClass("WorldBorder");
                    Constructor<?> WorldBorderConstructor = WorldBorder.getConstructor();
                    Method WorldBorder_setSize = WorldBorder.getMethod("setSize", double.class);
                    Method WorldBorder_setCenter = WorldBorder.getMethod("setCenter", double.class, double.class);
                    Field WorldBorder_world = WorldBorder.getDeclaredField("world");

                    Class<?> PacketPlayOutWorldBorder = Globals.reflectionUtil.getNMSClass("PacketPlayOutWorldBorder");
                    Class<?> EnumWorldBorderAction = PacketPlayOutWorldBorder.getDeclaredClasses()[0];
                    Constructor<?> PacketPlayOutWorldBorderConstructor = PacketPlayOutWorldBorder.getConstructor(
                            WorldBorder,
                            EnumWorldBorderAction
                    );

                    Class<?> EntityPlayer = Globals.reflectionUtil.getNMSClass("EntityPlayer");
                    Field EntityPlayer_playerConnection = EntityPlayer.getDeclaredField("playerConnection");

                    Class<?> PlayerConnection = Globals.reflectionUtil.getNMSClass("PlayerConnection");
                    Class<?> Packet = Globals.reflectionUtil.getNMSClass("Packet");
                    Method PlayerConnection_sendPacket = PlayerConnection.getMethod("sendPacket", Packet);

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
                            EnumWorldBorderAction.getField("INITIALIZE").get(null)
                    );

                    Object entityPlayer = EntityPlayer.cast(CraftPlayer_getHandle.invoke(craftPlayer));
                    Object playerConnection = PlayerConnection.cast(EntityPlayer_playerConnection.get(entityPlayer));
                    PlayerConnection_sendPacket.invoke(playerConnection, packet);

                } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
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
