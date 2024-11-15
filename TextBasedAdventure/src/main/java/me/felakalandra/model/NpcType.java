package me.felakalandra.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NpcType {
    FRIENDLY(0),
    HOSTILE(1);

    private final int value;

    NpcType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static NpcType fromString(String key) {
        if ("friendly".equalsIgnoreCase(key)) {
            return FRIENDLY;
        } else if ("hostile".equalsIgnoreCase(key)) {
            return HOSTILE;
        } else {
            throw new IllegalArgumentException("Invalid NpcType: " + key);
        }
    }
}
