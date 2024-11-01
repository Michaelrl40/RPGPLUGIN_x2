package com.example.minecraftrpg.resources;

public abstract class ClassResources {
    // Base stats that all classes will have but with different values
    private double maxHealth;
    private double maxMana;
    private double maxStamina;
    private double manaRegen;
    private double staminaRegen;

    // Current values
    private double currentMana;
    private double currentStamina;

    public ClassResources(double maxHealth, double maxMana, double maxStamina,
                          double manaRegen, double staminaRegen) {
        this.maxHealth = maxHealth;
        this.maxMana = maxMana;
        this.maxStamina = maxStamina;
        this.manaRegen = manaRegen;
        this.staminaRegen = staminaRegen;
        this.currentMana = maxMana;
        this.currentStamina = maxStamina;
    }

    // Getters and setters
    public double getMaxHealth() { return maxHealth; }
    public double getMaxMana() { return maxMana; }
    public double getMaxStamina() { return maxStamina; }
    public double getManaRegen() { return manaRegen; }
    public double getStaminaRegen() { return staminaRegen; }
    public double getCurrentMana() { return currentMana; }
    public double getCurrentStamina() { return currentStamina; }

    public void setCurrentMana(double mana) {
        this.currentMana = Math.min(maxMana, Math.max(0, mana));
    }

    public void setCurrentStamina(double stamina) {
        this.currentStamina = Math.min(maxStamina, Math.max(0, stamina));
    }

    // Resource management methods
    public boolean useMana(double amount) {
        if (currentMana >= amount) {
            currentMana -= amount;
            return true;
        }
        return false;
    }

    public boolean useStamina(double amount) {
        if (currentStamina >= amount) {
            currentStamina -= amount;
            return true;
        }
        return false;
    }

    public void regenResources() {
        currentMana = Math.min(maxMana, currentMana + manaRegen);
        currentStamina = Math.min(maxStamina, currentStamina + staminaRegen);
    }
}