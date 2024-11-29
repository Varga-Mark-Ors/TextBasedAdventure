package me.felakalandra.services;

import javafx.scene.control.Button;
import me.felakalandra.model.Dialogue;

import java.util.List;

public class DialogueService {
    // Fetch a random dialogue from the list
    public Dialogue getRandomDialogue(List<Dialogue> dialogues) {
        int randomIndex = (int) (Math.random() * dialogues.size());
        return dialogues.get(randomIndex);
    }

    // Set options for buttons based on dialogue
    public void setOptionButtons(Dialogue dialogue, Button option1, Button option2, Button option3) {
        if (dialogue.getOptions() != null) {
            if (dialogue.getOptions().containsKey("option1")) {
                option1.setText(dialogue.getOptions().get("option1").getText());
            }

            if (dialogue.getOptions().containsKey("option2")) {
                option2.setText(dialogue.getOptions().get("option2").getText());
            }

            if (dialogue.getOptions().containsKey("option3")) {
                option3.setText(dialogue.getOptions().get("option3").getText());
            }
        }
    }

}
