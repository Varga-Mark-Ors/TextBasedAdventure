package me.felakalandra.model;

import javafx.fxml.FXML;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Protagonist {
    @FXML
    private int health;

    @FXML
    private int gold;

    @FXML
    private int damagePoints;

    @FXML
    private boolean alive;

    @FXML
    private int level;

    public Protagonist() {
        this.health = 100;
        this.gold = 30;
        this.damagePoints = 20;
        this.alive = true;
        this.level = 1;
    }

    public void decreaseHealth(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.alive = false;
        }
    }

    public void runningGoldLose() {
        this.gold = (int)(this.gold*0.8);
    }

}
