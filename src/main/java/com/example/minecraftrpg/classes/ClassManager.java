package com.example.minecraftrpg.classes;

import com.example.minecraftrpg.MinecraftRPG;
import com.example.minecraftrpg.classes.classes.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassManager {
    private static ClassManager instance;
    private final MinecraftRPG plugin;
    private final Map<String, RPGClass> availableClasses;
    private final Map<UUID, String> playerClasses;

    private ClassManager(MinecraftRPG plugin) {
        this.plugin = plugin;
        this.availableClasses = new HashMap<>();
        this.playerClasses = new HashMap<>();
        registerClasses();
    }

    public static ClassManager getInstance(MinecraftRPG plugin) {
        if (instance == null) {
            instance = new ClassManager(plugin);
        }
        return instance;
    }

    private void registerClasses() {
        availableClasses.put("Warrior", new Warrior());
        availableClasses.put("Mage", new Mage());
        availableClasses.put("Archer", new Archer());
    }

    public boolean setPlayerClass(Player player, String className) {
        if (availableClasses.containsKey(className)) {
            playerClasses.put(player.getUniqueId(), className);
            plugin.getDatabase().savePlayerData(player, className);
            return true;
        }
        return false;
    }

    public String getPlayerClass(UUID uuid) {
        return playerClasses.getOrDefault(uuid, "Warrior"); // Default to Warrior if no class is set
    }

    public RPGClass getClass(String className) {
        return availableClasses.get(className);
    }

    public boolean hasClass(UUID uuid) {
        return playerClasses.containsKey(uuid);
    }

    public Map<String, RPGClass> getAvailableClasses() {
        return availableClasses;
    }

    public void loadPlayerClass(UUID uuid, String className) {
        if (availableClasses.containsKey(className)) {
            playerClasses.put(uuid, className);
        }
    }
}
