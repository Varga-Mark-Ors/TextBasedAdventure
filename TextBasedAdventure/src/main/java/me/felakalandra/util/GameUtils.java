package me.felakalandra.util;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import me.felakalandra.model.Protagonist;

public class GameUtils {
    public String replacePlaceholders(String dialogue, int price1, int price2) {
        // Replace the placeholders {number1} and {number2} with the provided price1 and price2 values.
        return dialogue.replace("{number1}", String.valueOf(price1))
                .replace("{number2}", String.valueOf(price2));
    }

    public String formattedReward(String rewardType1, String rewardType2, int number1, int number2) {
        // If the first reward type is "none", return a simplified message about the second reward.
        if ("none".equals(rewardType1)) {
            return "You will get: " + number2 + " " + rewardType2;
        }
        // Otherwise, include both the cost and the reward in the message.
        return "You will give: " + number1 + " " + rewardType1 + ". You will get: " + number2 + " " + rewardType2;
    }


    // Fade-out NPCS on the right side when they are leaving
    public static void fadeOut(ImageView imageView, double durationInSeconds) {
        if (imageView == null) return;

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(durationInSeconds), imageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> imageView.setVisible(false));
        fadeOut.play();
    }

    // Fade-in NPCS on the right side when they are arriving
    public static void fadeIn(ImageView imageView, double durationInSeconds) {
        if (imageView == null) return;

        imageView.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(durationInSeconds), imageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void updateProtagonistStats(Protagonist protagonist, Label hpLabel, Label goldLabel, Label damageLabel) {
        hpLabel.setText("HP: " + protagonist.getHealth());
        goldLabel.setText("Gold: " + protagonist.getGold());
        damageLabel.setText("Damage: " + protagonist.getDamagePoints());
    }

    public void setInitialLabels(Label hpLabel, Label goldLabel, Label damageLabel, Label daysLabel, Label levelLabel, Protagonist protagonist, int days) {
        hpLabel.setText("HP: " + protagonist.getHealth());
        goldLabel.setText("Gold: " + protagonist.getGold());
        damageLabel.setText("Damage: " + protagonist.getDamagePoints());
        daysLabel.setText("Days: " + days);
        levelLabel.setText("Level: " + protagonist.getLevel());
    }
}
