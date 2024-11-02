package com.example.minecraftrpg.classes;

import com.example.minecraftrpg.MinecraftRPG;
import com.example.minecraftrpg.classes.classes.Apprentice;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassManager implements Listener {
    private final MinecraftRPG plugin;
    private final Map<String, RPGClass> availableClasses = new HashMap<>();
    private final Map<UUID, String> playerClasses = new HashMap<>();

    public ClassManager(MinecraftRPG plugin) {
        this.plugin = plugin;
        registerClasses();
    }

    public String getPlayerClass(UUID uuid) {
        return playerClasses.get(uuid);
    }

    private void registerClasses() {
        // Register all classes here
        registerClass(new Apprentice());
        // Add more classes...
    }

    private void registerClass(RPGClass rpgClass) {
        availableClasses.put(rpgClass.getClassName().toLowerCase(), rpgClass);
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        String className = getPlayerClass(player.getUniqueId());
        if (className == null) return;

        RPGClass rpgClass = availableClasses.get(className.toLowerCase());
        if (rpgClass == null) return;

        // Check if player can use their weapon
        if (!rpgClass.canUseWeapon(player.getInventory().getItemInMainHand().getType())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Your class cannot use this weapon!");
        }
    }

    public boolean canAdvanceClass(Player player, String newClassName) {
        String currentClass = getPlayerClass(player.getUniqueId());
        if (currentClass == null) return false;

        RPGClass current = availableClasses.get(currentClass.toLowerCase());
        RPGClass newClass = availableClasses.get(newClassName.toLowerCase());

        if (current == null || newClass == null) return false;

        int level = plugin.getExperienceManager().getLevel(player.getUniqueId(), currentClass);

        // Check if player meets level requirements
        if (level < newClass.getTier().getMinLevel()) return false;

        // Check if new class is a valid upgrade option
        return current.getUpgradeOptions().contains(newClassName);
    }

    // Other existing methods...
}