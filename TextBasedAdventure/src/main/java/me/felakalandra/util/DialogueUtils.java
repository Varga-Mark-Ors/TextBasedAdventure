package me.felakalandra.util;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class DialogueUtils {

    // Show response logic
    public void showResponse(String response, Label npcResponseLabel, StackPane responseArea) {
        // Set NPC's response
        npcResponseLabel.setText(response);
        responseArea.setVisible(true);
        responseArea.setManaged(true);

        // Fade transition for the NPC's response
        FadeTransition fadeLabel = new FadeTransition(Duration.seconds(1), npcResponseLabel);
        fadeLabel.setFromValue(0);
        fadeLabel.setToValue(1);

        FadeTransition fadeArea = new FadeTransition(Duration.seconds(1), responseArea);
        fadeArea.setFromValue(0);
        fadeArea.setToValue(1);

        fadeLabel.play();
        fadeArea.play();
    }

    // Hide response logic
    public void hideResponseArea(Label npcResponseLabel, StackPane responseArea) {
        // Fade transition for hiding the NPC's response
        FadeTransition fadeOutLabel = new FadeTransition(Duration.seconds(1), npcResponseLabel);
        fadeOutLabel.setFromValue(1);
        fadeOutLabel.setToValue(0);

        FadeTransition fadeOutArea = new FadeTransition(Duration.seconds(1), responseArea);
        fadeOutArea.setFromValue(1);
        fadeOutArea.setToValue(0);

        fadeOutLabel.setOnFinished(event -> {
            // Hide the response area after fading out
            responseArea.setVisible(false);
            responseArea.setManaged(false);
            npcResponseLabel.setText("");
        });

        fadeOutLabel.play();
        fadeOutArea.play();
    }

    public void resetResponseArea(Label npcResponseLabel, StackPane responseArea) {
        responseArea.setVisible(false);
        responseArea.setManaged(false);
        npcResponseLabel.setText("");
    }
}
