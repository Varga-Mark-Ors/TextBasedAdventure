package me.felakalandra.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import me.felakalandra.util.GameApplication;
import org.tinylog.Logger;

import java.io.IOException;


public class MainMenuController {


    public void startNewGame(ActionEvent actionEvent) {
        try {
            GameApplication app = (GameApplication) GameApplication.getInstance();
            app.startGame(); // Change the scene from Main Menu into Game
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("Main Menu initialized");
    }

    @FXML
    private void exitGame() {
        Platform.exit();
        Logger.info("Exiting game");
    }
    @FXML
    public void loadGame(ActionEvent actionEvent) {
        Logger.info("Load game");
    }
    @FXML
    public void leaderboard(ActionEvent actionEvent) {
        Logger.info("Open leaderboard");
    }


}
