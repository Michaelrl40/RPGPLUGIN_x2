package com.example.minecraftrpg.attributes;

import java.util.EnumMap;
import java.util.Map;

public class PlayerAttributes {
    private final EnumMap<AttributeType, Integer> attributes;
    private int availablePoints;

    public PlayerAttributes() {
        this.attributes = new EnumMap<>(AttributeType.class);
        for (AttributeType type : AttributeType.values()) {
            attributes.put(type, 0);
        }
        this.availablePoints = 0;
    }

    public int getAttribute(AttributeType type) {
        return attributes.getOrDefault(type, 0);
    }

    public boolean addPoint(AttributeType type) {
        if (availablePoints > 0) {
            attributes.put(type, getAttribute(type) + 1);
            availablePoints--;
            return true;
        }
        return false;
    }

    public void addAvailablePoint() {
        availablePoints++;
    }

    public int getAvailablePoints() {
        return availablePoints;
    }

    public Map<AttributeType, Integer> getAllAttributes() {
        return new EnumMap<>(attributes);
    }

    public void setAvailablePoints(int points) {
        this.availablePoints = points;
    }

    public void setAttribute(AttributeType type, int value) {
        attributes.put(type, value);
    }
}
