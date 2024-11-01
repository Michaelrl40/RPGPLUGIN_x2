package com.example.minecraftrpg.leveling;

public class ClassExperience {
    private int level = 1;
    private int experience = 0;

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public void addExperience(int exp) {
        this.experience += exp;
    }
}
