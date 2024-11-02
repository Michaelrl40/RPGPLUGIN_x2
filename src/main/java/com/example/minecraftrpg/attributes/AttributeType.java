package com.example.minecraftrpg.attributes;

public enum AttributeType {
    STRENGTH("Strength", "Increases physical skill damage"),
    WISDOM("Wisdom", "Increases healing effectiveness"),
    INTELLECT("Intellect", "Increases magical skill damage"),
    DEXTERITY("Dexterity", "Increases projectile skill damage");

    private final String name;
    private final String description;

    AttributeType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
