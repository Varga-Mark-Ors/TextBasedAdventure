package me.felakalandra.services;

import me.felakalandra.model.Protagonist;
import me.felakalandra.model.RewardTask;

import java.util.ArrayList;
import java.util.List;

public class RewardService {
    GameLogicService gameLogicService = new GameLogicService();

    // List of pending rewards
    private final List<RewardTask> pendingRewards = new ArrayList<>();

    public void addEnemyReward(Protagonist protagonist, String type, int value){
        gameLogicService.modifyStat(protagonist, type, value);
    }

    // Add a reward to the pending list
    public void addPendingReward(String type, int value, double npcReliability) {
        boolean success = (Math.floor(Math.random() * 100) + 1) <= npcReliability;
        pendingRewards.add(new RewardTask(type, value, success));
    }

    // Apply all pending rewards
    public String applyPendingRewards(Protagonist protagonist) {
        StringBuilder feedback = new StringBuilder();

        if (pendingRewards.isEmpty()) {
            feedback.append("You were too hesitant to make a choice. The NPC left without giving you anything.");
        } else {
            for (RewardTask reward : pendingRewards) {
                if (reward.isSuccess()) {
                    gameLogicService.modifyStat(protagonist, reward.getType(), reward.getValue());
                    feedback.append("You received ")
                            .append(reward.getValue())
                            .append(" ")
                            .append(reward.getType())
                            .append(".\n");
                } else {
                    feedback.append("The NPC failed to deliver ")
                            .append(reward.getValue())
                            .append(" ")
                            .append(reward.getType())
                            .append(".\n");
                }
            }
            clearPendingRewards(); // Clear after applying rewards
        }

        return feedback.toString(); // Return feedback to display to the player
    }

    public void clearPendingRewards() {
        pendingRewards.clear();
    }
}