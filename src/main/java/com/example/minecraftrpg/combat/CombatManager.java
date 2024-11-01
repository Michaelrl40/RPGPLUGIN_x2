package com.example.minecraftrpg.combat;

import com.example.minecraftrpg.MinecraftRPG;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.UUID;

public class CombatManager implements Listener {
    private final MinecraftRPG plugin;
    private final HashMap<UUID, Long> combatTimers = new HashMap<>();
    private static final long COMBAT_DURATION = 10000; // 10 seconds in milliseconds

    public CombatManager(MinecraftRPG plugin) {
        this.plugin = plugin;
        startCombatChecker();
    }

    private void startCombatChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                combatTimers.entrySet().removeIf(entry -> {
                    UUID playerId = entry.getKey();
                    Player player = plugin.getServer().getPlayer(playerId);
                    if (player != null && currentTime - entry.getValue() > COMBAT_DURATION) {
                        exitCombat(player);
                        return true;
                    }
                    return false;
                });
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player defender) {
            enterCombat(defender);

            if (event.getDamager() instanceof Player attacker) {
                enterCombat(attacker);
            }
        }
    }

    @EventHandler
    public void onGeneralDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            enterCombat(player);
        }
    }

    public void enterCombat(Player player) {
        boolean wasInCombat = isInCombat(player);
        combatTimers.put(player.getUniqueId(), System.currentTimeMillis());

        if (!wasInCombat) {
            player.sendMessage(ChatColor.RED + "You have entered combat!");
        }
    }

    private void exitCombat(Player player) {
        player.sendMessage(ChatColor.GREEN + "You are no longer in combat!");
    }

    public boolean isInCombat(Player player) {
        Long combatTime = combatTimers.get(player.getUniqueId());
        if (combatTime == null) return false;
        return System.currentTimeMillis() - combatTime <= COMBAT_DURATION;
    }
}