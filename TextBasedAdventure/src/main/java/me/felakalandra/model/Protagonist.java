package me.felakalandra.model;

import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Protagonist {

    @FXML
    private int heartPoints;

    @FXML
    private int gold;

    @FXML
    private int damagePoints;

    @FXML
    private boolean alive;

    @FXML
    private int level;

    public Protagonist() {
        this.heartPoints = 100;
        this.gold = 30;
        this.damagePoints = 20;
        this.alive = true;
        this.level = 1;
    }
}
