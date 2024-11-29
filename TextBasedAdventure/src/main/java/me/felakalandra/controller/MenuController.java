package me.felakalandra.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import lombok.Setter;
import me.felakalandra.GameApplication;
import org.tinylog.Logger;

import java.io.IOException;


public class MenuController {

    @FXML
    public Button continueGame;

    @Setter
    private GameController gameController;

    @FXML
    public void continueCurrentGame(ActionEvent actionEvent) {
        Stage stage = (Stage) continueGame.getScene().getWindow();

        Logger.info("Game continued");

        stage.close();
    }

    @FXML
    public void startNewGame(ActionEvent actionEvent) {
        if (gameController != null) {
            gameController.resetGame();
        }

        Logger.info("New game started");
    }

    // Toggle sound on or off
    @FXML
    private void handleToggleSound() {
        gameController.toggleMute();
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

    @FXML
    public void returnToMainMenu(ActionEvent actionEvent) {
        try {
            if (gameController.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
                gameController.stopGameMusic();
            }
            // Close the game
            GameApplication app = (GameApplication) GameApplication.getInstance();
            app.getPrimaryStage().close();  // Close the current window.

            // Load the main menu.
            app.showMainMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("Returning to Main Menu");
    }
}