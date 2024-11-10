package me.felakalandra.util;

import javafx.fxml.FXML;
import me.felakalandra.model.Characters;
import me.felakalandra.model.MainCharacter;

public class GameRunner {
    @FXML
    MainCharacter hero =new MainCharacter();

    @FXML
    Characters characters = new Characters();

    public void runGame(){
        while(hero.isAlive()){
            System.out.println("Is alive");
            hero.setHeartPoints(0);
        }
        System.out.println("Is dead");
    }
}
