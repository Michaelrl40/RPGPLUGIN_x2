package com.example.minecraftrpg.leveling;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ExperienceManager {
    private static final int MAX_LEVEL = 50;
    private static final int BASE_XP_1_40 = 25000;
    private static final int BASE_XP_41_50 = 25000;
    private final HashMap<UUID, PlayerExperience> playerExp = new HashMap<>();

    public int calculateRequiredExperience(int level) {
        if (level < 1 || level > MAX_LEVEL) return -1;

        if (level <= 40) {
            // Total exp for levels 1-40 should be 13750
            // Using a progressive increase for each level
            // Base amount for level 1 would be smaller, increasing each level
            double baseExp = 200; // Starting exp for level 1
            double multiplier = 1.1; // Each level requires 10% more than the previous
            return (int)(baseExp * Math.pow(multiplier, level - 1));
        } else {
            // Levels 41-50 share 13750 exp, with steeper progression
            double baseExp = 800; // Starting exp for level 41
            double multiplier = 1.2; // Each level requires 20% more than the previous
            return (int)(baseExp * Math.pow(multiplier, level - 41));
        }
    }

    private void levelUp(Player player, int newLevel) {
        LevelUpEffect.play(player);
        player.sendMessage(ChatColor.GOLD + "≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈");
        player.sendMessage(ChatColor.YELLOW + "LEVEL UP! " + ChatColor.GOLD + "You are now level " + newLevel + "!");
        player.sendMessage(ChatColor.GOLD + "≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈");

        // Play level up sound
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    private void updateVanillaExpBar(Player player, ClassExperience cExp) {
        int currentLevel = cExp.getLevel();
        int currentExp = cExp.getExperience();
        int requiredExp = calculateRequiredExperience(currentLevel + 1);

        // Set the player's level number
        player.setLevel(currentLevel);

        // Simple progress calculation
        float progress = (float) currentExp / requiredExp;

        // Set the exp progress
        player.setExp(progress);
    }

    public void addExperience(Player player, String currentClass, int exp) {
        UUID uuid = player.getUniqueId();
        PlayerExperience pExp = playerExp.computeIfAbsent(uuid, k -> new PlayerExperience());

        if (!pExp.hasClass(currentClass)) {
            pExp.initializeClass(currentClass);
        }

        ClassExperience cExp = pExp.getClassExperience(currentClass);
        int currentLevel = cExp.getLevel();

        if (currentLevel >= MAX_LEVEL) {
            player.sendMessage(ChatColor.GOLD + "You are at max level!");
            return;
        }

        // Add the experience
        cExp.addExperience(exp);
        player.sendMessage(ChatColor.GREEN + "+" + exp + " XP");

        // Update exp bar right after gaining exp
        updateVanillaExpBar(player, cExp);

        // Check for level up
        int requiredExp = calculateRequiredExperience(currentLevel + 1);
        while (cExp.getExperience() >= requiredExp && currentLevel < MAX_LEVEL) {
            cExp.setExperience(cExp.getExperience() - requiredExp);
            currentLevel++;
            cExp.setLevel(currentLevel);
            levelUp(player, currentLevel);

            if (currentLevel < MAX_LEVEL) {
                requiredExp = calculateRequiredExperience(currentLevel + 1);
            }
        }

        // Update exp bar again after any level changes
        updateVanillaExpBar(player, cExp);
    }

    public void updatePlayerExpBar(Player player, String className) {
        UUID uuid = player.getUniqueId();
        if (playerExp.containsKey(uuid)) {
            PlayerExperience pExp = playerExp.get(uuid);
            if (pExp.hasClass(className)) {
                updateVanillaExpBar(player, pExp.getClassExperience(className));
            }
        }
    }

    public int getLevel(UUID uuid, String className) {
        if (playerExp.containsKey(uuid)) {
            PlayerExperience pExp = playerExp.get(uuid);
            if (pExp.hasClass(className)) {
                return pExp.getClassExperience(className).getLevel();
            }
        }
        return 1;
    }

    public int getExperience(UUID uuid, String className) {
        if (playerExp.containsKey(uuid)) {
            PlayerExperience pExp = playerExp.get(uuid);
            if (pExp.hasClass(className)) {
                return pExp.getClassExperience(className).getExperience();
            }
        }
        return 0;
    }

    public void setExperience(UUID uuid, String className, int level, int experience) {
        PlayerExperience pExp = playerExp.computeIfAbsent(uuid, k -> new PlayerExperience());
        if (!pExp.hasClass(className)) {
            pExp.initializeClass(className);
        }
        ClassExperience cExp = pExp.getClassExperience(className);
        cExp.setLevel(level);
        cExp.setExperience(experience);
    }
}