package me.felakalandra.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.tinylog.Logger;

public class MenuController {
    @FXML
    public Button continueGame;

    // Continue the current game
    @FXML
    public void continueCurrentGame(ActionEvent actionEvent) {
        Stage stage = (Stage) continueGame.getScene().getWindow();
        stage.close();
    }

    // Start a new game
    @FXML
    public void startNewGame(ActionEvent actionEvent) {
        Logger.info("New game started");
    }

    // Toggle sound on or off
    @FXML
    private void handleToggleSound() {
        Logger.info("Sound toggled");
    }

    // Show the options menu
    @FXML
    public void showOptions(ActionEvent actionEvent) {
        Logger.info("Options menu opened");
    }

    // Exit the game
    @FXML
    private void exitGame() {
        Platform.exit();
    }
}