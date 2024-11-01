package com.example.minecraftrpg.commands;

import com.example.minecraftrpg.MinecraftRPG;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelCommand implements CommandExecutor {
    private final MinecraftRPG plugin;

    public LevelCommand(MinecraftRPG plugin) {
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

        // Get player's level and experience
        int level = plugin.getExperienceManager().getLevel(player.getUniqueId(), currentClass);
        int currentExp = plugin.getExperienceManager().getExperience(player.getUniqueId(), currentClass);
        int requiredExp = plugin.getExperienceManager().calculateRequiredExperience(level + 1);

        // Send level information
        player.sendMessage(ChatColor.GOLD + "=== Level Information ===");
        player.sendMessage(ChatColor.YELLOW + "Class: " + ChatColor.WHITE + currentClass);
        player.sendMessage(ChatColor.YELLOW + "Level: " + ChatColor.WHITE + level);
        player.sendMessage(ChatColor.YELLOW + "Experience: " + ChatColor.WHITE + currentExp +
                ChatColor.GRAY + "/" + ChatColor.WHITE + requiredExp);
        player.sendMessage(ChatColor.YELLOW + "Progress to Next Level: " + ChatColor.WHITE +
                String.format("%.1f", (currentExp * 100.0 / requiredExp)) + "%");

        return true;
    }
}