package com.raeic.embarker;

import com.raeic.embarker.auth.events.PlayerAuthListeners;
import com.raeic.embarker.auth.state.ServerStartupState;
import com.raeic.embarker.db.DB;
import com.raeic.embarker.land.commands.LandCommand;
import com.raeic.embarker.land.commands.StakeCommand;
import com.raeic.embarker.land.commands.UnstakeCommand;
import com.raeic.embarker.land.events.BlockChangeListeners;
import com.raeic.embarker.land.events.EntityBlockChangeListeners;
import com.raeic.embarker.land.events.HangingChangeListeners;
import com.raeic.embarker.land.models.StakedChunkManager;
import com.raeic.embarker.land.schedulers.LandScheduler;
import com.raeic.embarker.party.commands.*;
import com.raeic.embarker.party.models.PartyManager;
import com.raeic.embarker.player.commands.DebugCommand;
import com.raeic.embarker.player.events.PlayerLoadListeners;
import com.raeic.embarker.player.models.EmbarkerPlayerManager;
import com.raeic.embarker.reflections.util.ReflectionManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.sql.SQLException;

public class Embarker extends JavaPlugin {
    public Embarker() {
        super();
    }

    /**
     * This constructor is needed for MockBukkit
     */
    public Embarker(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        Globals.plugin = this;
        Globals.reflectionManager = new ReflectionManager();

        Globals.embarkerPlayers = new EmbarkerPlayerManager();
        Globals.party = new PartyManager();
        Globals.stakedChunks = new StakedChunkManager();

        Globals.landScheduler = new LandScheduler();

        // Set up commands
        this.getCommand("debug").setExecutor(new DebugCommand());

        this.getCommand("land").setExecutor(new LandCommand());
        this.getCommand("stake").setExecutor(new StakeCommand());
        this.getCommand("unstake").setExecutor(new UnstakeCommand());

        this.getCommand("disband").setExecutor(new DisbandCommand());
        this.getCommand("dismiss").setExecutor(new DismissCommand());
        this.getCommand("invite").setExecutor(new InviteCommand());
        this.getCommand("join").setExecutor(new JoinCommand());
        this.getCommand("leave").setExecutor(new LeaveCommand());
        this.getCommand("party").setExecutor(new PartyCommand());

        // Set up listeners
        this.getServer().getPluginManager().registerEvents(new PlayerAuthListeners(), this);

        this.getServer().getPluginManager().registerEvents(new BlockChangeListeners(), this);
        this.getServer().getPluginManager().registerEvents(new EntityBlockChangeListeners(), this);
        this.getServer().getPluginManager().registerEvents(new HangingChangeListeners(), this);

        this.getServer().getPluginManager().registerEvents(new PlayerLoadListeners(), this);

        // Get plugin file configuration
        FileConfiguration pluginConfig = this.getConfig();

        String url = pluginConfig.getString("db.url");
        String username = pluginConfig.getString("db.username");
        String password = pluginConfig.getString("db.password");

        try {
            DB.setupConnection(url, username, password);
            DB.getConnection();
            DB.setupTables();
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
        Bukkit.getScheduler().cancelTasks(this);
    }
}
