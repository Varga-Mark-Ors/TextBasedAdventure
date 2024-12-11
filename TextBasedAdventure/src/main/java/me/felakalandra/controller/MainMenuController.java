package me.felakalandra.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import me.felakalandra.GameApplication;
import me.felakalandra.util.save.GameState;
import me.felakalandra.util.save.SaveManager;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class MainMenuController {

    @FXML
    public Button handleToggleSoundButton;

    @FXML
    public VBox mainMenuBox;

    @FXML
    public VBox optionsMenuBox;

    @FXML
    public Button exitOptionsButton;

    @FXML
    public VBox savedGamesBox;
    private final MediaPlayer mediaPlayer;
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

    // Load the game from MenuController
    @FXML
    public void loadSavedGame(ActionEvent actionEvent) {
        // Load the saved game files from the SavedGames folder.
        File saveDir = new File("TextBasedAdventure/src/main/resources/SavedGames");
        File[] savedGames = saveDir.listFiles((dir, name) -> name.endsWith(".json")); // Csak JSON fájlok

        // Remove the previous buttons but keep the Label.
        savedGamesBox.getChildren().removeIf(node -> node instanceof Button);

        if (savedGames != null && savedGames.length > 0) {
            // A mentett játékokat tartalmazó gombok létrehozása
            for (File saveFile : savedGames) {
                Button button = new Button(saveFile.getName().replace(".json", ""));
                button.setOnAction(event -> loadGameFromFile(saveFile));  // Betöltjük a játékot a gombokkal
                savedGamesBox.getChildren().add(button);  // Hozzáadjuk a gombot a meglévő tartalomhoz
            }
        } else {
            // If there are no saved games then alert the player.
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No saved games");
            alert.setHeaderText(null);
            alert.setContentText("There are no saved games available.");
            alert.showAndWait();
        }
        // Create the exitSavedGamesButton dynamically and add it last.
        Button exitButton = new Button("Exit Saved Games");
        exitButton.setOnAction(this::exitSavedGames);
        savedGamesBox.getChildren().add(exitButton);

        // Show the saved games box and hide the options menu
        savedGamesBox.setVisible(true);
        savedGamesBox.setManaged(true);
        optionsMenuBox.setVisible(false);
        optionsMenuBox.setManaged(false);
    }

    private void loadGameFromFile(File saveFile) {
        SaveManager saveManager = new SaveManager();
        GameState loadedState = saveManager.loadGame(saveFile.getAbsolutePath());

        if (loadedState != null) {
            try {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.stop();
                }
                // We continue the game based on the saved state.
                GameApplication app = (GameApplication) GameApplication.getInstance();
                app.startGame();
                GameController gameController = app.getGameController();
                gameController.initializeFromGameState(loadedState);

                Logger.info("Successfully loaded saved game: " + saveFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Logger.error("Failed to load saved game from file: " + saveFile.getName());
        }
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
    public void exitGame() {
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
    public void leaderboard(ActionEvent actionEvent) {
        Logger.info("Open leaderboard");
    }

    @FXML
    public void initialize() {
        playMenuMusic();  // When the main menu is loaded, the music should start playing
    }

    @FXML
    public void exitSavedGames(ActionEvent actionEvent) {
        savedGamesBox.setVisible(false);
        savedGamesBox.setManaged(false);
        optionsMenuBox.setVisible(true);
        optionsMenuBox.setManaged(true);
    }

}
