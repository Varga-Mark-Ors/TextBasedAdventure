package me.felakalandra.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import me.felakalandra.util.GameApplication;
import org.tinylog.Logger;
import java.io.IOException;
import java.util.Objects;


public class MainMenuController {

    private final MediaPlayer mediaPlayer;
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
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // ooping music
            mediaPlayer.play();
        }
    }
    @FXML
    private void exitGame() {
        Platform.exit();
        Logger.info("Exiting game");
        mediaPlayer.stop();
    }
    @FXML
    public void loadGame(ActionEvent actionEvent) {
        Logger.info("Load game");
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
