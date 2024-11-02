package com.example.minecraftrpg.classes;

public enum ClassTier {
    BEGINNER(1, 15),      // Level 1-15
    ADVANCED(15, 40),     // Level 15-40
    MASTER(40, 50);       // Level 40-50

    private final int minLevel;
    private final int maxLevel;

    ClassTier(int minLevel, int maxLevel) {
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    public int getMinLevel() { return minLevel; }
    public int getMaxLevel() { return maxLevel; }
}
