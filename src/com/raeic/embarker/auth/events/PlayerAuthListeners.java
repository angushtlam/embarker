package com.raeic.embarker.auth.events;

import com.raeic.embarker.auth.models.EmbarkerUser;
import com.raeic.embarker.auth.state.ServerStartupState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.Timestamp;
import java.time.Instant;

public class PlayerAuthListeners implements Listener {

    /**
     * Early in the player initialization process
     * @param event PlayerLoginEvent
     */
    @EventHandler
    public void handlePlayerLogin(PlayerLoginEvent event) {
        Player p = event.getPlayer();

        if (!ServerStartupState.instance.isReady()) {
            p.kickPlayer(ServerStartupState.instance.getMessage());
            return;
        }
    }

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        // Find if EmbarkerPlayer row exists and create one if they don't.
        Player p = event.getPlayer();
        EmbarkerUser user = EmbarkerUser.findOne(p);

        // Change join message according to their time logged into server.
        if (user != null) {
            event.setJoinMessage("Welcome back, " + p.getDisplayName() + ".");
        } else {
            event.setJoinMessage("Welcome to the world, " + p.getDisplayName() + "!");
            user = new EmbarkerUser(p);

            // If the user is we should store their first played time into the DB
            user.setFirstLogin(new Timestamp(p.getFirstPlayed()));
        }

        // Record changes and save
        user.setLatestLogin(new Timestamp(Instant.now().toEpochMilli()));
        user.save();
    }

}