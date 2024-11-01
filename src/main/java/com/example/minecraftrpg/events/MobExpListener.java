package com.example.minecraftrpg.events;

import com.example.minecraftrpg.MinecraftRPG;
import com.example.minecraftrpg.leveling.ExperienceManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class MobExpListener implements Listener {
    private final ExperienceManager expManager;
    private final MinecraftRPG plugin;

    public MobExpListener(MinecraftRPG plugin, ExperienceManager expManager) {
        this.plugin = plugin;
        this.expManager = expManager;
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        // Check if killer is a player
        if (event.getEntity().getKiller() == null) {
            return;
        }

        Player killer = event.getEntity().getKiller();

        // Get the current class from your class manager
        String currentClass = plugin.getClassManager().getPlayerClass(killer.getUniqueId());
        if (currentClass == null) {
            killer.sendMessage(ChatColor.RED + "You need to select a class first!");
            return;
        }

        Entity entity = event.getEntity();

        // Check if entity is hostile and award XP
        if (isHostile(entity)) {
            expManager.addExperience(killer, currentClass, 10);
        }


    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        event.setAmount(0); // Prevent vanilla exp gain
    }

    private boolean isHostile(Entity entity) {
        return entity instanceof Monster || // Covers most hostile mobs
                entity instanceof Phantom ||
                entity instanceof Slime ||
                entity instanceof Ghast ||
                entity instanceof Shulker ||
                entity instanceof MagmaCube;
    }
}