package me.felakalandra.services;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import me.felakalandra.model.Protagonist;
import org.tinylog.Logger;

import java.util.Random;

public class GameLogicService {
    private final Random random = new Random();

    private final Image protagonistImageLevel2 = new Image("Images/Protagonist/Main2.png");
    private final Image protagonistImageLevel3 = new Image("Images/Protagonist/Main3.png");
    private final Image protagonistImageLevel4 = new Image("Images/Protagonist/Main4.png");
    private final Image protagonistImageLevel5 = new Image("Images/Protagonist/Main5.png");

    public void levelUp(Protagonist protagonist, ImageView protagonistLeft, Label levelLabel, ObjectiveService objectiveService) {
        int totalPoints = protagonist.getHealth() + protagonist.getDamagePoints();

        if (totalPoints > 200 && protagonist.getLevel() < 2) {
            protagonistLeft.setImage(protagonistImageLevel2);
            protagonist.setLevel(2);
            levelLabel.setText("Level: " + protagonist.getLevel());
            objectiveService.onProtagonistLevelChange(); // Triggered only for level 2
        }

        if (totalPoints > 300 && protagonist.getLevel() < 3) {
            protagonistLeft.setImage(protagonistImageLevel3);
            protagonist.setLevel(3);
            levelLabel.setText("Level: " + protagonist.getLevel());
        }

        if (totalPoints > 450 && protagonist.getLevel() < 4) {
            protagonistLeft.setImage(protagonistImageLevel4);
            protagonist.setLevel(4);
            levelLabel.setText("Level: " + protagonist.getLevel());
        }

        if (totalPoints > 600 && protagonist.getLevel() < 5) {
            protagonistLeft.setImage(protagonistImageLevel5);
            protagonist.setLevel(5);
            levelLabel.setText("Level: " + protagonist.getLevel());
        }
    }

    // Modify protagonist stats
    public void modifyStat(Protagonist protagonist, String stat, int value) {
        switch (stat) {
            case "gold" -> protagonist.setGold(protagonist.getGold() + value);
            case "damage" -> protagonist.setDamagePoints(protagonist.getDamagePoints() + value);
            case "hp" -> protagonist.setHealth(protagonist.getHealth() + value);
            default -> Logger.error("Unrecognized stat: " + stat);
        }
    }

    // Method to check if the protagonist has enough resources for a specific requirement.
    public boolean hasEnoughResources(Protagonist protagonist, String stat, int required) {
        // Validate input to prevent null values.
        if (stat == null) {
            Logger.error("stat is null. Cannot determine resource requirements.");
            return false; // Return false or handle this case appropriately.
        }

        // Determine which stat to check based on the input string.
        return switch (stat) {
            case "gold" -> protagonist.getGold() >= required;
            case "damage" -> protagonist.getDamagePoints() >= required;
            case "hp" -> protagonist.getHealth() >= required;
            default -> {
                Logger.error("Unrecognized stat: " + stat);
                yield false; // Return false for unrecognized stat types.
            }
        };
    }


    // Generate a random number within a range based on the specified stat.
    public int generateNumber(String stat, Protagonist protagonist, double npcReliability, int days) {
        int min, max;
        double reliabilityFactor = 0.8 + ((100 - npcReliability) * 0.002);

        switch (stat) {
            case "gold" -> {
                min = Math.max(1, (int) (protagonist.getGold() * 0.15));
                max = Math.max(min + 1, (int) (protagonist.getGold() * 0.25));
                int baseGoldReward = min + random.nextInt(max - min + 1);

                // Dynamic boost for under-leveled gold
                double goldBoostFactor = 1.0;
                if (protagonist.getGold() < (protagonist.getHealth() / 2) ||
                        protagonist.getGold() < (protagonist.getDamagePoints() * 2)) {
                    goldBoostFactor = 1.1;
                }

                // Late-game boost
                if (days > 35) {
                    baseGoldReward *= 1.2;
                }

                return Math.max(1, (int) (baseGoldReward * goldBoostFactor * reliabilityFactor * increasingDifficulty(days)));
            }
            case "damage" -> {
                min = Math.max(1, (int) (protagonist.getDamagePoints() * 0.2 + 3));
                max = Math.max(min + 1, (int) (protagonist.getDamagePoints() * 0.3 + 6));
                int baseDamageReward = min + random.nextInt(max - min + 1);

                // Boost based on difficulty
                double damageDifficultyFactor = increasingDifficulty(days) * 1.1;

                return Math.max(1, (int) (baseDamageReward * reliabilityFactor * damageDifficultyFactor));
            }
            case "hp" -> {
                min = Math.max(1, (int) (protagonist.getHealth() * 0.08));
                max = Math.max(min + 1, (int) (protagonist.getHealth() * 0.15));
                int baseHpReward = min + random.nextInt(max - min + 1);

                // Dynamic boost for under-leveled HP
                double hpBoostFactor = 1.0;
                if (protagonist.getHealth() < (protagonist.getGold() / 2) ||
                        protagonist.getHealth() < (protagonist.getDamagePoints() * 5)) {
                    hpBoostFactor = 1.2;
                }

                // Late-game boost
                if (days > 35) {
                    baseHpReward *= 1.1;
                }

                return Math.max(1, (int) (baseHpReward * hpBoostFactor * reliabilityFactor * increasingDifficulty(days)));
            }
            default -> {
                Logger.error("Unrecognized stat: " + stat);
                return 0;
            }
        }
    }

    // Returns a double value that will help increase the difficulty of the game by days passed
    public double increasingDifficulty(int days) {
        // Early game: Start closer to 1 and scale faster
        if (days < 10) {
            return 0.9 + (0.01 * days); // Starts at 0.9, reaches 1.0 on day 10
        }

        // Cap difficulty multiplier to 2.5 for late game
        if (days > 46) {
            return 2.5;
        }

        // Mid-game progression: logarithmic scaling for smooth growth
        return 1 + Math.log(1 + days) / Math.log(10);
    }
}