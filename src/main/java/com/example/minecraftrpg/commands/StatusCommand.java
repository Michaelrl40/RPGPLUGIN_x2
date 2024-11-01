package com.example.minecraftrpg.commands;

import com.example.minecraftrpg.MinecraftRPG;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatusCommand implements CommandExecutor {
    private final MinecraftRPG plugin;

    public StatusCommand(MinecraftRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        String currentClass = plugin.getClassManager().getPlayerClass(player.getUniqueId());
        if (currentClass == null) {
            player.sendMessage(ChatColor.RED + "You need to select a class first!");
            return true;
        }

        // Get resources from ResourceManager
        showPlayerStatus(player);
        return true;
    }

    private void showPlayerStatus(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Status ===");
        player.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.WHITE +
                plugin.getClassManager().getPlayerClass(player.getUniqueId()));

        // Get resource info from ResourceManager
        player.sendMessage(ChatColor.BLUE + "Mana: " + ChatColor.WHITE +
                String.format("%.1f/%.1f",
                        plugin.getResourceManager().getCurrentMana(player),
                        plugin.getResourceManager().getMaxMana(player)));

        player.sendMessage(ChatColor.GREEN + "Stamina: " + ChatColor.WHITE +
                String.format("%.1f/%.1f",
                        plugin.getResourceManager().getCurrentStamina(player),
                        plugin.getResourceManager().getMaxStamina(player)));

        player.sendMessage(ChatColor.RED + "Health: " + ChatColor.WHITE +
                String.format("%.1f/%.1f", player.getHealth(), player.getMaxHealth()));
    }
}