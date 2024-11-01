package com.example.minecraftrpg.resources;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.UUID;
import com.example.minecraftrpg.MinecraftRPG;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import com.example.minecraftrpg.resources.classes.WarriorResources;
import com.example.minecraftrpg.resources.classes.MageResources;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.EventPriority;

public class ResourceManager implements Listener {
    private final MinecraftRPG plugin;
    private final HashMap<UUID, ClassResources> playerResources = new HashMap<>();
    private static final int BAR_LENGTH = 20;


    public ResourceManager(MinecraftRPG plugin) {
        this.plugin = plugin;
        startResourceRegen();
    }

    public void initializePlayerResources(Player player, String className) {
        ClassResources resources;
        switch (className.toLowerCase()) {
            case "warrior":
                resources = new WarriorResources();
                break;
            case "mage":
                resources = new MageResources();
                break;
            default:
                resources = new WarriorResources(); // Default fallback
                break;
        }
        playerResources.put(player.getUniqueId(), resources);

        // Set max health for the player
        player.setMaxHealth(resources.getMaxHealth());
        player.setHealth(resources.getMaxHealth());

        // Set food level to 19 (one below max) to allow eating
        player.setFoodLevel(19);
        player.setSaturation(0.0f);
        updateResourceBars(player);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true); // Prevent vanilla hunger changes
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setFoodLevel(19);
        player.setSaturation(5.0f);
    }

    private void startResourceRegen() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : playerResources.keySet()) {
                    Player player = plugin.getServer().getPlayer(uuid);
                    if (player != null && player.isOnline()) {
                        ClassResources resources = playerResources.get(uuid);
                        // Fast stamina regen
                        double currentStamina = resources.getCurrentStamina();
                        if (currentStamina < resources.getMaxStamina()) {
                            resources.setCurrentStamina(Math.min(resources.getMaxStamina(),
                                    currentStamina + resources.getStaminaRegen() * 2)); // Doubled regen rate
                        }
                        // Normal mana regen
                        double currentMana = resources.getCurrentMana();
                        if (currentMana < resources.getMaxMana()) {
                            resources.setCurrentMana(Math.min(resources.getMaxMana(),
                                    currentMana + resources.getManaRegen()));
                        }
                        updateResourceBars(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 10L); // Update every half second instead of every second
    }

    private void updateResourceBars(Player player) {
        ClassResources resources = playerResources.get(player.getUniqueId());
        if (resources == null) return;

        // Update mana bar in action bar
        String manaBar = createResourceBar(resources.getCurrentMana(), resources.getMaxMana(),
                ChatColor.BLUE, "MANA");

        // Update the hunger/stamina bar
        updateStaminaBar(player, resources);

        // Only show mana in action bar now
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent(manaBar));
    }

    private String createResourceBar(double current, double max, ChatColor color, String name) {
        int filled = (int) ((current / max) * BAR_LENGTH);
        StringBuilder bar = new StringBuilder(color + name + ": " + ChatColor.DARK_GRAY + "[");

        for (int i = 0; i < BAR_LENGTH; i++) {
            if (i < filled) {
                bar.append(color).append("│");
            } else {
                bar.append(ChatColor.GRAY).append("│");
            }
        }

        bar.append(ChatColor.DARK_GRAY).append("] ")
                .append(color).append((int)current).append("/").append((int)max);

        return bar.toString();
    }

    private void updateStaminaBar(Player player, ClassResources resources) {
        // Convert stamina to food level (Minecraft food level is 0-15 instead of 0-20)
        int foodLevel = (int) ((resources.getCurrentStamina() / resources.getMaxStamina()) * 19);
        float saturation = 0.0f; // Keep saturation high to prevent hunger effects

        player.setFoodLevel(foodLevel);
        player.setSaturation(saturation);
    }

    private void startFoodLevelChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    player.setFoodLevel(19);
                    player.setSaturation(0.0f);
                }
            }
        }.runTaskTimer(plugin, 0L, 10L); // Check every half second
    }

    // Methods to use resources
    public boolean useMana(Player player, double amount) {
        ClassResources resources = playerResources.get(player.getUniqueId());
        if (resources != null && resources.useMana(amount)) {
            updateResourceBars(player);
            return true;
        }
        return false;
    }

    public boolean useStamina(Player player, double amount) {
        ClassResources resources = playerResources.get(player.getUniqueId());
        if (resources != null && resources.useStamina(amount)) {
            updateResourceBars(player);
            return true;
        }
        return false;
    }

    public double getCurrentMana(Player player) {
        ClassResources resources = playerResources.get(player.getUniqueId());
        return resources != null ? resources.getCurrentMana() : 0;
    }

    public double getMaxMana(Player player) {
        ClassResources resources = playerResources.get(player.getUniqueId());
        return resources != null ? resources.getMaxMana() : 0;
    }

    public double getCurrentStamina(Player player) {
        ClassResources resources = playerResources.get(player.getUniqueId());
        return resources != null ? resources.getCurrentStamina() : 0;
    }

    public double getMaxStamina(Player player) {
        ClassResources resources = playerResources.get(player.getUniqueId());
        return resources != null ? resources.getMaxStamina() : 0;
    }

}