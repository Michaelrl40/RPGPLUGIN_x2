package com.example.minecraftrpg.classes;

public abstract class RPGClass {
    private final String className;
    private final String description;

    public RPGClass(String className, String description) {
        this.className = className;
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public String getDescription() {
        return description;
    }
}
