package com.example.minecraftrpg.classes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;

public abstract class RPGClass {
    private final String className;
    private final String description;
    private final ClassTier tier;
    private final Set<Material> allowedWeapons = new HashSet<>();
    private final Set<Material> allowedArmor = new HashSet<>();
    private final Set<String> upgradeOptions = new HashSet<>();  // Next tier class options

    // Base stats per level
    protected final double baseHealth;
    protected final double healthPerLevel;
    protected final double baseMana;
    protected final double manaPerLevel;
    protected final double baseStamina;
    protected final double staminaPerLevel;

    public RPGClass(String className, String description, ClassTier tier,
                    double baseHealth, double healthPerLevel,
                    double baseMana, double manaPerLevel,
                    double baseStamina, double staminaPerLevel) {
        this.className = className;
        this.description = description;
        this.tier = tier;
        this.baseHealth = baseHealth;
        this.healthPerLevel = healthPerLevel;
        this.baseMana = baseMana;
        this.manaPerLevel = manaPerLevel;
        this.baseStamina = baseStamina;
        this.staminaPerLevel = staminaPerLevel;

        initializeAllowedEquipment();
        initializeUpgradeOptions();
    }

    // To be implemented by each class
    protected abstract void initializeAllowedEquipment();
    protected abstract void initializeUpgradeOptions();

    // Calculate stats based on level
    public double getMaxHealth(int level) {
        return baseHealth + (healthPerLevel * (level - 1));
    }

    public double getMaxMana(int level) {
        return baseMana + (manaPerLevel * (level - 1));
    }

    public double getMaxStamina(int level) {
        return baseStamina + (staminaPerLevel * (level - 1));
    }

    // Equipment checks
    public boolean canUseWeapon(Material weapon) {
        return allowedWeapons.contains(weapon);
    }

    public boolean canUseArmor(Material armor) {
        return allowedArmor.contains(armor);
    }

    protected void addAllowedWeapon(Material weapon) {
        allowedWeapons.add(weapon);
    }

    protected void addAllowedArmor(Material armor) {
        allowedArmor.add(armor);
    }

    protected void addUpgradeOption(String className) {
        upgradeOptions.add(className);
    }

    // Getters
    public String getClassName() {
        return className;
    }

    public String getDescription() {
        return description;
    }

    public ClassTier getTier() {
        return tier;
    }

    public Set<String> getUpgradeOptions() {
        return new HashSet<>(upgradeOptions);
    }
}
