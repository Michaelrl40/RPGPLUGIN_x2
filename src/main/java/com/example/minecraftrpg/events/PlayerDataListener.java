package com.example.minecraftrpg.events;

import com.example.minecraftrpg.MinecraftRPG;
import com.example.minecraftrpg.database.DatabaseManager;
import com.example.minecraftrpg.leveling.ExperienceManager;
import org.bukkit.entity.Player;  // Add this import
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.example.minecraftrpg.classes.ClassManager;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDataListener implements Listener {
    private final MinecraftRPG plugin;
    private final DatabaseManager database;
    private final ExperienceManager expManager;

    public PlayerDataListener(MinecraftRPG plugin, DatabaseManager database, ExperienceManager expManager) {
        this.plugin = plugin;
        this.database = database;
        this.expManager = expManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // First load class from database
        String currentClass = database.loadCurrentClass(event.getPlayer().getUniqueId());
        if (currentClass != null) {
            // Initialize attributes
            plugin.getAttributeManager().initializePlayer(event.getPlayer());
            // Load their class experience
            database.loadClassExperience(event.getPlayer().getUniqueId(), currentClass, expManager);
            // Update their exp bar with the loaded data
            expManager.updatePlayerExpBar(event.getPlayer(), currentClass);
            // You'll add more data loading here later (attributes, skills, etc.)
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String currentClass = plugin.getClassManager().getPlayerClass(event.getPlayer().getUniqueId());
        if (currentClass != null) {
            // Save all their data
            database.savePlayerData(event.getPlayer(), currentClass);
            database.saveClassExperience(
                    event.getPlayer().getUniqueId(),
                    currentClass,
                    expManager.getLevel(event.getPlayer().getUniqueId(), currentClass),
                    expManager.getExperience(event.getPlayer().getUniqueId(), currentClass)
            );
            // You'll add more data saving here later (attributes, skills, etc.)
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String currentClass = plugin.getClassManager().getPlayerClass(player.getUniqueId());

        // Keep experience and level on death
        event.setKeepLevel(true);
        event.setDroppedExp(0);

        // Update the exp bar after respawn
        new BukkitRunnable() {
            @Override
            public void run() {
                if (currentClass != null) {
                    expManager.updatePlayerExpBar(player, currentClass);
                }
            }
        }.runTaskLater(plugin, 1L); // Run 1 tick after respawn
    }

}

