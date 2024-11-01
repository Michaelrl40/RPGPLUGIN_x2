package com.example.minecraftrpg.leveling;

import java.util.HashMap;

public class PlayerExperience {
    private final HashMap<String, ClassExperience> classExperience = new HashMap<>();

    public void initializeClass(String className) {
        classExperience.put(className, new ClassExperience());
    }

    public boolean hasClass(String className) {
        return classExperience.containsKey(className);
    }

    public ClassExperience getClassExperience(String className) {
        return classExperience.get(className);
    }
}