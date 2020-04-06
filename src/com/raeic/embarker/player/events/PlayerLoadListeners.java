package com.raeic.embarker.player.events;

import com.raeic.embarker.Globals;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerLoadListeners implements Listener {
    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        // Load the player model on login
        Player p = event.getPlayer();
        Globals.embarkerPlayers.findOne(p.getUniqueId().toString());
    }
}