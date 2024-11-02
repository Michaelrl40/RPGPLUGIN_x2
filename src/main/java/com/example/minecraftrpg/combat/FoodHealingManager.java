package com.example.minecraftrpg.combat;

import com.example.minecraftrpg.MinecraftRPG;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

import java.util.HashMap;

public class FoodHealingManager implements Listener {
    private final MinecraftRPG plugin;
    private final CombatManager combatManager;
    private final HashMap<Material, Double> foodHealing = new HashMap<>();
    private final Set<UUID> healingPlayers = new HashSet<>();


    public FoodHealingManager(MinecraftRPG plugin, CombatManager combatManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
        initializeFoodHealing();
    }

    private void initializeFoodHealing() {
        // Meats
        foodHealing.put(Material.COOKED_BEEF, 8.0);
        foodHealing.put(Material.COOKED_PORKCHOP, 8.0);
        foodHealing.put(Material.COOKED_MUTTON, 7.0);
        foodHealing.put(Material.COOKED_CHICKEN, 6.0);
        foodHealing.put(Material.COOKED_RABBIT, 6.0);
        foodHealing.put(Material.COOKED_COD, 5.0);
        foodHealing.put(Material.COOKED_SALMON, 6.0);

        // Other foods
        foodHealing.put(Material.BREAD, 4.0);
        foodHealing.put(Material.GOLDEN_APPLE, 10.0);
        foodHealing.put(Material.ENCHANTED_GOLDEN_APPLE, 20.0);
        foodHealing.put(Material.GOLDEN_CARROT, 6.0);
        foodHealing.put(Material.APPLE, 3.0);
        foodHealing.put(Material.BAKED_POTATO, 4.0);
        foodHealing.put(Material.BEETROOT_SOUP, 5.0);
        foodHealing.put(Material.MUSHROOM_STEW, 5.0);
    }

    @EventHandler
    public void onFoodConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack food = event.getItem();

        // Cancel vanilla food mechanics
        event.setCancelled(true);

        if (healingPlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must wait for your current healing to complete!");
            return;
        }

        // Check if in combat
        if (combatManager.isInCombat(player)) {
            player.sendMessage(ChatColor.RED + "You cannot eat while in combat!");
            return;
        }

        // Get healing amount
        Double healAmount = foodHealing.get(food.getType());
        if (healAmount == null) return;

        healingPlayers.add(player.getUniqueId());

        new HealingTask(plugin, player, healAmount, 5) {
            @Override
            public void onComplete() {
                healingPlayers.remove(player.getUniqueId());
            }
        }.runTaskTimer(plugin, 0L, 1L);

        player.sendMessage(ChatColor.GREEN + "You begin to heal from eating " +
                formatFoodName(food.getType().name()));

        // Remove one food item
        if (food.getAmount() > 1) {
            food.setAmount(food.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            // Prevent healing from full hunger bar
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(false);  // Allow changes
        event.setFoodLevel(19);     // Keep it at 19
    }

    private String formatFoodName(String name) {
        return name.toLowerCase()
                .replace('_', ' ')
                .substring(0, 1).toUpperCase() +
                name.toLowerCase().replace('_', ' ').substring(1);
    }
}
