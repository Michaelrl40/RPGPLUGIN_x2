package com.example.minecraftrpg.attributes;

import com.example.minecraftrpg.MinecraftRPG;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;

public class AttributeManager {
    private final MinecraftRPG plugin;
    private final HashMap<UUID, PlayerAttributes> playerAttributes = new HashMap<>();

    public AttributeManager(MinecraftRPG plugin) {
        this.plugin = plugin;
    }

    public void initializePlayer(Player player) {
        playerAttributes.putIfAbsent(player.getUniqueId(), new PlayerAttributes());
        player.sendMessage(ChatColor.GRAY + "Debug: Player attributes initialized!");
    }

    public void addAttributePoint(Player player) {
        PlayerAttributes attrs = playerAttributes.get(player.getUniqueId());
        if (attrs == null) {
            player.sendMessage(ChatColor.RED + "Debug: Player attributes not initialized!");
            initializePlayer(player);  // Auto-initialize if missing
            attrs = playerAttributes.get(player.getUniqueId());
        }

        if (attrs != null) {
            attrs.addAvailablePoint();
            player.sendMessage(ChatColor.GREEN + "You gained an attribute point! Use /attribute spend to spend it.");
            player.sendMessage(ChatColor.GRAY + "Debug: Current points: " + attrs.getAvailablePoints());
        }
    }

    public boolean spendPoint(Player player, AttributeType type) {
        PlayerAttributes attrs = playerAttributes.get(player.getUniqueId());
        if (attrs != null && attrs.addPoint(type)) {
            player.sendMessage(ChatColor.GREEN + "Successfully added a point to " + type.getName() + "!");
            return true;
        }
        return false;
    }

    public PlayerAttributes getPlayerAttributes(UUID playerId) {
        return playerAttributes.get(playerId);
    }

    public void saveAttributes(Player player) {
        // Will implement database saving later
    }

    public void loadAttributes(Player player) {
        // Will implement database loading later
    }
}