package com.example.minecraftrpg.commands;

import com.example.minecraftrpg.MinecraftRPG;
import com.example.minecraftrpg.classes.RPGClass;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClassCommand implements CommandExecutor {
    private final MinecraftRPG plugin;

    public ClassCommand(MinecraftRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        if (args.length == 0) {
            showClassHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "select":
                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /class select <className>");
                    return true;
                }
                selectClass(player, args[1]);
                break;

            case "info":
                if (args.length == 2) {
                    showClassInfo(player, args[1]);
                } else {
                    showCurrentClassInfo(player);
                }
                break;

            case "list":
                listClasses(player);
                break;

            default:
                showClassHelp(player);
                break;
        }

        return true;
    }

    private void selectClass(Player player, String className) {
        if (!plugin.getClassManager().getAvailableClasses().containsKey(className)) {
            player.sendMessage(ChatColor.RED + "Invalid class! Use /class list to see available classes.");
            return;
        }

        if (plugin.getClassManager().setPlayerClass(player, className)) {
            player.sendMessage(ChatColor.GREEN + "You are now a " + className + "!");
            // Update their exp bar for the new class
            plugin.getExperienceManager().updatePlayerExpBar(player, className);
        } else {
            player.sendMessage(ChatColor.RED + "Failed to set class. Please try again.");
        }
    }

    private void showClassInfo(Player player, String className) {
        RPGClass rpgClass = plugin.getClassManager().getClass(className);
        if (rpgClass == null) {
            player.sendMessage(ChatColor.RED + "Invalid class! Use /class list to see available classes.");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "=== " + rpgClass.getClassName() + " ===");
        player.sendMessage(ChatColor.YELLOW + rpgClass.getDescription());
    }

    private void showCurrentClassInfo(Player player) {
        String currentClass = plugin.getClassManager().getPlayerClass(player.getUniqueId());
        if (currentClass == null) {
            player.sendMessage(ChatColor.RED + "You haven't selected a class yet! Use /class select <className>");
            return;
        }

        RPGClass rpgClass = plugin.getClassManager().getClass(currentClass);
        player.sendMessage(ChatColor.GOLD + "=== Your Class: " + currentClass + " ===");
        player.sendMessage(ChatColor.YELLOW + rpgClass.getDescription());
    }

    private void listClasses(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Available Classes ===");
        for (RPGClass rpgClass : plugin.getClassManager().getAvailableClasses().values()) {
            player.sendMessage(ChatColor.YELLOW + "- " + rpgClass.getClassName() + ": " +
                    ChatColor.WHITE + rpgClass.getDescription());
        }
    }

    private void showClassHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Class Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/class select <className>" + ChatColor.WHITE + " - Select a class");
        player.sendMessage(ChatColor.YELLOW + "/class info [className]" + ChatColor.WHITE + " - View class info");
        player.sendMessage(ChatColor.YELLOW + "/class list" + ChatColor.WHITE + " - List all available classes");
    }
}
