package com.raeic.embarker;

import com.raeic.embarker.auth.events.PlayerAuthListeners;
import com.raeic.embarker.auth.state.ServerStartupState;
import com.raeic.embarker.cities.commands.StakeCommand;
import com.raeic.embarker.cities.commands.UnstakeCommand;
import com.raeic.embarker.db.DB;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Embarker extends JavaPlugin {
    public static Embarker plugin = null;

    @Override
    public void onEnable() {
        super.onEnable();

        Embarker.plugin = this;

        // Set up commands
        this.getCommand("stake").setExecutor(new StakeCommand());
        this.getCommand("unstake").setExecutor(new UnstakeCommand());

        // Set up listeners
        this.getServer().getPluginManager().registerEvents(new PlayerAuthListeners(), this);

        // Get plugin file configuration
        FileConfiguration pluginConfig = this.getConfig();

        String url = pluginConfig.getString("db.url");
        String username = pluginConfig.getString("db.username");
        String password = pluginConfig.getString("db.password");

        try {
            DB.setup(url, username, password);
            DB.getConnection();
            ServerStartupState.instance.setReady(true);
            ServerStartupState.instance.setMessage("");
        } catch (SQLException e) {
            ServerStartupState.instance.setMessage("Failed to connect to database. Try connecting later!");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        DB.closeConnection();
    }
}
