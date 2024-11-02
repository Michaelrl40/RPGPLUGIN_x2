package com.example.minecraftrpg.classes.classes;

import com.example.minecraftrpg.classes.RPGClass;
import org.bukkit.Material;
import com.example.minecraftrpg.classes.ClassTier;

public class Warrior extends RPGClass {
    public Warrior() {
        super(
                "Apprentice",
                "A novice in fighting, learning the basics of swords and shields",
                ClassTier.BEGINNER,
                100.0,  // Base Health
                10.0,   // Health per level
                100.0,  // Base Mana
                20.0,   // Mana per level
                100.0,  // Base Stamina
                5.0     // Stamina per level
        );
    }

    @Override
    protected void initializeAllowedEquipment() {
        // Weapons
        addAllowedWeapon(Material.WOODEN_SWORD);
        addAllowedWeapon(Material.STICK);

        // Armor
        addAllowedArmor(Material.LEATHER_HELMET);
        addAllowedArmor(Material.LEATHER_CHESTPLATE);
        addAllowedArmor(Material.LEATHER_LEGGINGS);
        addAllowedArmor(Material.LEATHER_BOOTS);
    }

    @Override
    protected void initializeUpgradeOptions() {
        addUpgradeOption("Wizard");
        addUpgradeOption("Battlemage");
    }
}
