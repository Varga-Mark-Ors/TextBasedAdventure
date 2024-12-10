package me.felakalandra.services;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import me.felakalandra.model.Protagonist;
import org.tinylog.Logger;

public class ObjectiveService {

    @Getter @Setter
    private int npcInteractions = 0;
    @Getter @Setter
    private boolean isDragonDead = false;

    private static final String CHECKED_CHECKBOX = "Styles/Icons/checked-checkbox.png";
    private static final String UNCHECKED_CHECKBOX = "Styles/Icons/unchecked-checkbox.png";

    private final Protagonist protagonist;
    private final ImageView objectiveImage1;
    private final ImageView objectiveImage2;
    private final ImageView objectiveImage3;

    public ObjectiveService(Protagonist protagonist, ImageView objectiveImage1, ImageView objectiveImage2, ImageView objectiveImage3) {
        this.protagonist = protagonist;
        this.objectiveImage1 = objectiveImage1;
        this.objectiveImage2 = objectiveImage2;
        this.objectiveImage3 = objectiveImage3;
    }

    // Method to update all objectives
    public void updateObjectives() {
        updateObjective1();
        updateObjective2();
        updateObjective3();
    }

    // Update individual objectives
    private void updateObjective1() {
        if (protagonist.getLevel() >= 3) {
            setChecked(objectiveImage1);
        } else {
            setUnchecked(objectiveImage1);
        }
    }

    private void updateObjective2() {
        if (npcInteractions >= 30) {
            setChecked(objectiveImage2);
        } else {
            setUnchecked(objectiveImage2);
        }
    }

    private void updateObjective3() {
        if (isDragonDead) {
            setChecked(objectiveImage3);
        } else {
            setUnchecked(objectiveImage3);
        }
    }

    // Helper methods to set checkbox states
    private void setChecked(ImageView objectiveImage) {
        objectiveImage.setImage(new Image(CHECKED_CHECKBOX));
        Logger.info("Objective checked: " + objectiveImage);
    }

    private void setUnchecked(ImageView objectiveImage) {
        objectiveImage.setImage(new Image(UNCHECKED_CHECKBOX));
        Logger.info("Objective unchecked: " + objectiveImage);
    }

    // Methods for specific events
    public void onProtagonistLevelChange() {
        updateObjective1();
    }

    public void onNpcInteraction() {
        npcInteractions++;
        updateObjective2();
    }

    public void onDragonDeath() {
        isDragonDead = true;
        updateObjective3();
    }
}
