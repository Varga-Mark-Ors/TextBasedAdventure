package me.felakalandra.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import lombok.Setter;
import me.felakalandra.GameApplication;
import org.tinylog.Logger;

import java.io.IOException;


public class MenuController {

    @FXML
    public Button continueGame;
    @FXML
    public Button handleToggleSoundButton;

    @FXML
    public Button saveGameButton;
    @FXML
    public Button leaderboardButton;
    @FXML
    public Button exitOptionsButton;

    @FXML
    private VBox menuBox;

    @FXML
    private VBox optionsMenuBox;

    @Setter
    private GameController gameController;
    private boolean isMuted = false;


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
    private void handleToggleSound()
    {
        if (isMuted){
            gameController.toggleMute();
            handleToggleSoundButton.setText("Toggle Sound: ON");
            Logger.info("Sound toggled");
        } else{
            gameController.toggleMute();
            handleToggleSoundButton.setText("Toggle Sound: OFF");
            Logger.info("Sound toggled");
        }
        isMuted = !isMuted;
    }



    // Show the options menu
    @FXML
    public void showOptions(ActionEvent actionEvent) {
        Logger.info("Options menu opened");

        // Hide main buttons
        menuBox.setVisible(false);
        menuBox.setManaged(false);
        optionsMenuBox.setVisible(true);
        optionsMenuBox.setManaged(true);
    }

    @FXML
    public void saveGame(ActionEvent actionEvent) {
        Logger.info("Game saved");
        // Save game logic goes here
    }

    @FXML
    public void showLeaderboard(ActionEvent actionEvent) {
        Logger.info("Leaderboard opened");
        // Leaderboard logic goes here
    }

    @FXML
    public void exitOptions(ActionEvent actionEvent) {
        Logger.info("Exiting options menu");

        // Show main menu and hide options menu
        optionsMenuBox.setVisible(false);
        optionsMenuBox.setManaged(false);
        menuBox.setVisible(true);
        menuBox.setManaged(true);
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

            // Close the game window
            GameApplication app = (GameApplication) GameApplication.getInstance();
            Stage gameStage = app.getPrimaryStage();
            if (gameStage != null) {
                gameStage.close();
            }

            // Check that menu window is opened
            Stage menuStage = (Stage) continueGame.getScene().getWindow();
            if (menuStage != null) {
                menuStage.close();  // Close the menu window if it's opened
            }

            // display the main menu
            app.showMainMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("Return to the main menu");
    }
}