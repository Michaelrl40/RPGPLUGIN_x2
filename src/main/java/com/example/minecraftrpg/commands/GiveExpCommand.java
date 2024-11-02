package com.example.minecraftrpg.commands;

import com.example.minecraftrpg.MinecraftRPG;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveExpCommand implements CommandExecutor {
    private final MinecraftRPG plugin;

    public GiveExpCommand(MinecraftRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("minecraftrpg.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        // Check args length
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /giveexp <player> <amount>");
            return true;
        }

        // Get player
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        // Parse amount
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                sender.sendMessage(ChatColor.RED + "Amount must be greater than 0!");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount!");
            return true;
        }

        // Get player's current class
        String currentClass = plugin.getClassManager().getPlayerClass(target.getUniqueId());
        if (currentClass == null) {
            sender.sendMessage(ChatColor.RED + "Target player doesn't have a class selected!");
            return true;
        }

        // Calculate current level and exp needed
        int currentLevel = plugin.getExperienceManager().getLevel(target.getUniqueId(), currentClass);
        int requiredExp = plugin.getExperienceManager().calculateRequiredExperience(currentLevel + 1);
        int currentExp = plugin.getExperienceManager().getExperience(target.getUniqueId(), currentClass);

        // Calculate how much exp is needed to level up
        int expNeededToLevel = requiredExp - currentExp;

        // If requested exp is more than needed to level, only give what's needed
        if (amount > expNeededToLevel) {
            amount = expNeededToLevel;
        }

        // Give experience
        plugin.getExperienceManager().addExperience(target, currentClass, amount);
        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " experience to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You need " + (requiredExp - (currentExp + amount)) +
                " more exp to reach level " + (currentLevel + 1));

        return true;
    }
}