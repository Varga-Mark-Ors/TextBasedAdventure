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

    public void levelUp(Protagonist protagonist, ImageView protagonistLeft, Label levelLabel) {
        if (((protagonist.getHealth() + protagonist.getDamagePoints()) > 200) && (protagonist.getLevel() < 2)) {
            protagonistLeft.setImage(protagonistImageLevel2);
            protagonist.setLevel(2);
            levelLabel.setText("Level: " + protagonist.getLevel());
        }

        if (((protagonist.getHealth() + protagonist.getDamagePoints()) > 300) && (protagonist.getLevel() < 3)) {
            protagonistLeft.setImage(protagonistImageLevel3);
            protagonist.setLevel(3);
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
    public int generateNumber(String stat, Protagonist protagonist, double npcReliability) {
        int min, max;

        // Calculate the range as 10% to 30% of the protagonist's current values.
        switch (stat) {
            case "gold" -> {
                min = (int) (protagonist.getGold() * 0.2);
                max = (int) (protagonist.getGold() * 0.3);
            }
            case "damage" -> {
                min = (int) (protagonist.getDamagePoints() * 0.2);
                max = (int) (protagonist.getDamagePoints() * 0.3);
            }
            case "hp" -> {
                min = (int) (protagonist.getHealth() * 0.2);
                max = (int) (protagonist.getHealth() * 0.3);
            }
            default -> {
                System.err.println("Unrecognized stat: " + stat);
                return 0;
            }
        }

        // Returns a random value within the range and weighted by the reliability of the character
        return (int) ((min + random.nextInt(max - min + 1)) * ((100 - npcReliability) * 0.1));
    }
}