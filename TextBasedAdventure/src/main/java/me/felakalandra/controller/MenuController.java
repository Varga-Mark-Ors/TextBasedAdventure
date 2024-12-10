package me.felakalandra.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import lombok.Setter;
import me.felakalandra.GameApplication;
import me.felakalandra.util.save.GameState;
import me.felakalandra.util.save.SaveManager;
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
    public VBox returnToMainMenuConfirmationBox;

    @FXML
    private VBox menuBox;

    @FXML
    private VBox optionsMenuBox;

    @FXML
    private VBox exitConfirmationBox;

    @FXML
    private Label menuLabel;

    @FXML
    private Label optionsLabel;

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

        // Update labels
        menuLabel.setVisible(false);
        optionsLabel.setVisible(true);

        // Hide main buttons
        menuBox.setVisible(false);
        menuBox.setManaged(false);
        optionsMenuBox.setVisible(true);
        optionsMenuBox.setManaged(true);
    }

    @FXML
    public void showLeaderboard(ActionEvent actionEvent) {
        Logger.info("Leaderboard opened");
        // Leaderboard logic goes here
    }

    @FXML
    public void exitOptions(ActionEvent actionEvent) {
        Logger.info("Exiting options menu");

        // Update labels
        optionsLabel.setVisible(false);
        menuLabel.setVisible(true);

        // Show main menu and hide options menu
        optionsMenuBox.setVisible(false);
        optionsMenuBox.setManaged(false);
        menuBox.setVisible(true);
        menuBox.setManaged(true);
    }


    // Exit the game
    @FXML
    private void exitGame() {
        Logger.info("Exit game button clicked");

        // Hide the main menu and options menu
        menuBox.setVisible(false);
        menuBox.setManaged(false);
        optionsMenuBox.setVisible(false);
        optionsMenuBox.setManaged(false);

        // Show the exit confirmation box
        exitConfirmationBox.setVisible(true);
        exitConfirmationBox.setManaged(true);
    }

    // Handle No button click
    @FXML
    private void noExit(ActionEvent actionEvent) {
        Logger.info("User canceled exit");

        // Hide the exit confirmation box and show the main menu or options
        exitConfirmationBox.setVisible(false);
        exitConfirmationBox.setManaged(false);

        // Show the main menu or options menu
        menuBox.setVisible(true);
        menuBox.setManaged(true);
    }

    // Handle Yes button click
    @FXML
    private void yesExit(ActionEvent actionEvent) {
        Logger.info("User confirmed exit");

        // Close the application
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

    public void noExitToMainMenu(ActionEvent actionEvent) {
        Logger.info("User canceled exit");

        // Hide the exit confirmation box and show the main menu or options
        returnToMainMenuConfirmationBox.setVisible(false);
        returnToMainMenuConfirmationBox.setManaged(false);

        // Show the main menu or options menu
        menuBox.setVisible(true);
        menuBox.setManaged(true);
    }

    public void showConfirmation(ActionEvent actionEvent) {
        menuBox.setVisible(false);
        menuBox.setManaged(false);

        // Show the main menu or options menu
        returnToMainMenuConfirmationBox.setVisible(true);
        returnToMainMenuConfirmationBox.setManaged(true);
    }
    @FXML
    public void saveGame(ActionEvent actionEvent) {
        SaveManager saveManager = new SaveManager();
        saveManager.saveGame(gameController); // Pass the GameController instance
        Logger.info("Game saved");
    }
}