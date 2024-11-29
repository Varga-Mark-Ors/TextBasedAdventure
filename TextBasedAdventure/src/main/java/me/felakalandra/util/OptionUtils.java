package me.felakalandra.util;

import javafx.scene.control.Button;

public class OptionUtils {
    public void showOptionButtons(Button option1, Button option2, Button option3) {
        option1.setVisible(true);
        option2.setVisible(true);
        option3.setVisible(true);
    }

    public void hideOptionButtons(Button option1, Button option2, Button option3) {
        option1.setVisible(false);
        option2.setVisible(false);
        option3.setVisible(false);
    }
}
