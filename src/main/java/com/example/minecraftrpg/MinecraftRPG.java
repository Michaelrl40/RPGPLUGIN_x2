package com.example.minecraftrpg;

import com.example.minecraftrpg.combat.CombatManager;
import com.example.minecraftrpg.combat.FoodHealingManager;
import com.example.minecraftrpg.commands.*;
import com.example.minecraftrpg.events.MobExpListener;
import com.example.minecraftrpg.resources.ResourceManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.example.minecraftrpg.database.DatabaseManager;
import com.example.minecraftrpg.leveling.ExperienceManager;
import com.example.minecraftrpg.events.PlayerDataListener;
import com.example.minecraftrpg.classes.ClassManager;
import org.bukkit.entity.Player;
import com.example.minecraftrpg.attributes.AttributeManager;

public class MinecraftRPG extends JavaPlugin {
    private DatabaseManager database;
    private ExperienceManager expManager;
    private ClassManager classManager;
    private ResourceManager resourceManager;
    private CombatManager combatManager;
    private FoodHealingManager foodHealingManager;
    private AttributeManager attributeManager;

    @Override
    public void onEnable() {
        try {
            // Initialize managers
            database = new DatabaseManager(this);
            if (database == null) {
                getLogger().severe("Failed to initialize DatabaseManager!");
                return;
            }

            expManager = new ExperienceManager(this);
            if (expManager == null) {
                getLogger().severe("Failed to initialize ExperienceManager!");
                return;
            }

            classManager = new ClassManager(this);
            if (classManager == null) {
                getLogger().severe("Failed to initialize ClassManager!");
                return;
            }

            resourceManager = new ResourceManager(this);
            if (resourceManager == null) {
                getLogger().severe("Failed to initialize ResourceManager!");
                return;
            }

            // Initialize new managers
            combatManager = new CombatManager(this);
            if (combatManager == null) {
                getLogger().severe("Failed to initialize CombatManager!");
                return;
            }

            foodHealingManager = new FoodHealingManager(this, combatManager);
            if (foodHealingManager == null) {
                getLogger().severe("Failed to initialize FoodHealingManager!");
                return;
            }

            // Add the new AttributeManager
            attributeManager = new AttributeManager(this);
            if (attributeManager == null) {
                getLogger().severe("Failed to initialize AttributeManager!");
                return;
            }

            // Register listeners
            getServer().getPluginManager().registerEvents(
                    new PlayerDataListener(this, database, expManager),
                    this
            );

            getServer().getPluginManager().registerEvents(
                    new MobExpListener(this, expManager),
                    this
            );

            // Register all manager listeners
            getServer().getPluginManager().registerEvents(resourceManager, this);
            getServer().getPluginManager().registerEvents(foodHealingManager, this);
            getServer().getPluginManager().registerEvents(combatManager, this);

            getLogger().info("MinecraftRPG has been enabled!");

            // Register commands
            getCommand("class").setExecutor(new ClassCommand(this));
            getCommand("level").setExecutor(new LevelCommand(this));
            getCommand("status").setExecutor(new StatusCommand(this));
            getCommand("attribute").setExecutor(new AttributeCommand(this));
            getCommand("giveexp").setExecutor(new GiveExpCommand(this));// Add this line

        } catch (Exception e) {
            getLogger().severe("Error during plugin initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void onDisable() {
        // Save all online players' data before shutting down
        for (Player player : getServer().getOnlinePlayers()) {
            String currentClass = classManager.getPlayerClass(player.getUniqueId());
            if (currentClass != null) {
                database.savePlayerData(player, currentClass);
                database.saveClassExperience(
                        player.getUniqueId(),
                        currentClass,
                        expManager.getLevel(player.getUniqueId(), currentClass),
                        expManager.getExperience(player.getUniqueId(), currentClass)
                );
            }
        }

        if (database != null) {
            database.close();
        }
        getLogger().info("MinecraftRPG has been disabled!");
    }

    public DatabaseManager getDatabase() {
        return database;
    }

    public ExperienceManager getExperienceManager() {
        return expManager;
    }

    public ClassManager getClassManager() {
        return classManager;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    public AttributeManager getAttributeManager() {
        return attributeManager;
    }
}