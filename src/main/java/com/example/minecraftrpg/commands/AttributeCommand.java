package com.example.minecraftrpg.commands;

import com.example.minecraftrpg.MinecraftRPG;
import com.example.minecraftrpg.attributes.AttributeType;
import com.example.minecraftrpg.attributes.PlayerAttributes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AttributeCommand implements CommandExecutor {
    private final MinecraftRPG plugin;

    public AttributeCommand(MinecraftRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        if (args.length == 0) {
            showAttributeHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info":
                showAttributes(player);
                break;
            case "spend":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /attribute spend <attribute>");
                    return true;
                }
                spendPoint(player, args[1]);
                break;
            default:
                showAttributeHelp(player);
                break;
        }

        return true;
    }

    private void showAttributes(Player player) {
        PlayerAttributes attrs = plugin.getAttributeManager().getPlayerAttributes(player.getUniqueId());
        if (attrs == null) return;

        player.sendMessage(ChatColor.GOLD + "=== Your Attributes ===");
        for (AttributeType type : AttributeType.values()) {
            player.sendMessage(ChatColor.YELLOW + type.getName() + ": " +
                    ChatColor.WHITE + attrs.getAttribute(type));
        }
        player.sendMessage(ChatColor.GREEN + "Available Points: " +
                attrs.getAvailablePoints());
    }

    private void spendPoint(Player player, String attributeName) {
        try {
            AttributeType type = AttributeType.valueOf(attributeName.toUpperCase());
            if (plugin.getAttributeManager().spendPoint(player, type)) {
                showAttributes(player);
            } else {
                player.sendMessage(ChatColor.RED + "No attribute points available!");
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid attribute! Valid attributes are: " +
                    String.join(", ", AttributeType.values().toString()));
        }
    }

    private void showAttributeHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Attribute Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/attribute info" +
                ChatColor.WHITE + " - View your attributes");
        player.sendMessage(ChatColor.YELLOW + "/attribute spend <attribute>" +
                ChatColor.WHITE + " - Spend an attribute point");
    }
}