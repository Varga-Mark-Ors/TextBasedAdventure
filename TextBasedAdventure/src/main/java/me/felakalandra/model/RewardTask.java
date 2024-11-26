package me.felakalandra.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RewardTask {
    private final String type;
    private final int value;
    private final boolean success;
}

