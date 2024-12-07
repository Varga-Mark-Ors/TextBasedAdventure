package me.felakalandra.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import me.felakalandra.GameApplication;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Objects;


public class MainMenuController {

    @FXML
    public Button handleToggleSoundButton;
    private final MediaPlayer mediaPlayer;
    @FXML
    public VBox mainMenuBox;
    @FXML
    public VBox optionsMenuBox;
    @FXML
    public Button exitOptionsButton;
    public Label mainMenuLabel;
    public Label mainMenuOptionsLabel;
    private boolean isMuted = false;

    public MainMenuController() {
        // Set the main menu music.
        Media media = new Media(Objects.requireNonNull(getClass().getResource("/Sounds/Main_Menu_Sound.mp3")).toExternalForm());
        mediaPlayer = new MediaPlayer(media);
    }

    public void startNewGame(ActionEvent actionEvent) {
        try {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.stop();
            }
            GameApplication app = (GameApplication) GameApplication.getInstance();
            app.startGame(); // Change the scene from Main Menu into Game
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("Main Menu initialized");
    }

    public void playMenuMusic() {
        if (mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); //looping music
            mediaPlayer.play();
        }
    }

    public void handleToggleSound() {
        if (isMuted) {
            mediaPlayer.setVolume(0.5); // Restore to default volume
            handleToggleSoundButton.setText("Toggle Sound: ON");
        } else {
            mediaPlayer.setVolume(0.0); // Mute
            handleToggleSoundButton.setText("Toggle Sound: OFF");
        }
        isMuted = !isMuted;
    }

    @FXML
    private void exitGame() {
        Platform.exit();
        Logger.info("Exiting game");
        mediaPlayer.stop();
    }

    @FXML
    public void showOptions(ActionEvent actionEvent) {
        Logger.info("Options menu opened");

        // Update labels
        mainMenuLabel.setVisible(false);
        mainMenuOptionsLabel.setVisible(true);

        // Hide main buttons
        mainMenuBox.setVisible(false);
        mainMenuBox.setManaged(false);
        optionsMenuBox.setVisible(true);
        optionsMenuBox.setManaged(true);
    }

    @FXML
    public void exitOptions(ActionEvent actionEvent) {
        Logger.info("Exiting options menu");

        // Update labels
        mainMenuOptionsLabel.setVisible(false);
        mainMenuLabel.setVisible(true);

        // Show main menu and hide options menu
        optionsMenuBox.setVisible(false);
        optionsMenuBox.setManaged(false);
        mainMenuBox.setVisible(true);
        mainMenuBox.setManaged(true);
    }
    @FXML
    public void loadSavedGame(ActionEvent actionEvent) {
        Logger.info("Load Saved Game");
    }

    @FXML
    public void leaderboard(ActionEvent actionEvent) {
        Logger.info("Open leaderboard");
    }

    @FXML
    public void initialize() {
        playMenuMusic();  // When the main menu is loaded, the music should start playing
    }

}
