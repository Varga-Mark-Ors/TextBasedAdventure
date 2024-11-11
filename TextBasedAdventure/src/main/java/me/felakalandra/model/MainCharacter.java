package me.felakalandra.model;

import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainCharacter {

    @FXML
    private int heartPoints;

    @FXML
    private int gold;

    @FXML
    private int damagePoints;

    @FXML
    private boolean alive;

    public MainCharacter() {
        this.heartPoints = 30;
        this.gold = 20;
        this.damagePoints = 20;
        this.alive = true;
    }
}
