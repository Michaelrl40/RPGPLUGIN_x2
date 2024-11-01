package com.example.minecraftrpg.resources.classes;

import com.example.minecraftrpg.resources.ClassResources;

public class WarriorResources extends ClassResources {
    public WarriorResources() {
        super(
                100.0,  // maxHealth
                50.0,   // maxMana
                100.0,  // maxStamina (changed to 100 for percentage-based system)
                1.0,    // manaRegen
                5.0     // staminaRegen
        );
    }
}
