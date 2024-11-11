package me.felakalandra.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GameController {
    private static final LocalTime INITIAL_GAME_TIME = LocalTime.of(7, 0);
    private static final String DAY_BACKGROUND_PATH = "/Images/day_background.jpg";
    private static final String NIGHT_BACKGROUND_PATH = "/Images/night_background.jpg";
    private static final String MAIN_CHARACTER_PATH = "Images/maincharachter/charachter1.png";
    private static final String SIDE_CHARACTERS_DIR = "/Images/sidecharachters";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private AnchorPane gameBase;

    @FXML
    private ImageView gameBackground;

    @FXML
    private Label daysLabel;

    @FXML
    private Label timeCurrent;

    @FXML
    private Label timeDay;

    @FXML
    private Button menuButton;

    @FXML
    public ImageView characterLeft;

    @FXML
    public ImageView characterRight;

    private LocalTime gameTime;
    private int days = 0;

    // Background images
    private final Image dayBackground = new Image(DAY_BACKGROUND_PATH);
    private final Image nightBackground = new Image(NIGHT_BACKGROUND_PATH);

    // Main character image (always displayed on the left)
    private final Image mainCharacter = new Image(MAIN_CHARACTER_PATH);

    // List of side character images to cycle through on the right
    private List<Image> sideCharacters = new ArrayList<>();
    private int currentSideCharacterIndex = 0;

    // Initialize the game controller
    @FXML
    private void initialize() {
        gameBase.setBackground(Background.fill(Color.LIGHTBLUE));

        // Initialize game time to 7:00 AM
        gameTime = INITIAL_GAME_TIME;
        updateGameTimeDisplay();

        // Set initial background and main character
        gameBackground.setImage(dayBackground);

        // Set initial background and main character
        characterLeft.setImage(mainCharacter);

        // Load side characters from directory
        loadSideCharacters();
        if (!sideCharacters.isEmpty()) {
            characterRight.setImage(sideCharacters.get(currentSideCharacterIndex));
        }

        // Start game clock for simulation
        startGameClock();

        Logger.info("GameController initialized");
    }

    // Start the game clock
    private void startGameClock() {
        // 10 IRL seconds for each in-game minute
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> advanceTime()));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    // Load side characters from the directory
    private void loadSideCharacters() {
        try {
            Path sideCharacterDir = Paths.get(Objects.requireNonNull(getClass().getResource(SIDE_CHARACTERS_DIR)).toURI());
            if (Files.isDirectory(sideCharacterDir)) {
                sideCharacters = Files.list(sideCharacterDir)
                        .filter(path -> path.toString().toLowerCase().endsWith(".png"))
                        .map(path -> new Image(path.toUri().toString()))
                        .collect(Collectors.toList());
            } else {
                Logger.error("Side character directory not found" + sideCharacterDir);
            }
        } catch (IOException | URISyntaxException e) {
            Logger.error("Error loading side characters: " + e.getMessage());
        }
    }

    // Cycle to the next side character
    private void cycleSideCharacter() {
        if (sideCharacters.isEmpty()) {
            Logger.error("No side characters available to cycle.");
            return;
        }

        // Cycle to the next character in the list
        currentSideCharacterIndex = (currentSideCharacterIndex + 1) % sideCharacters.size();
        characterRight.setImage(sideCharacters.get(currentSideCharacterIndex));
    }

    private void advanceTime() {
        // Increase game time by 1 minute
        gameTime = gameTime.plusMinutes(1);

        // Check if a new day has begun
        if (gameTime.equals(LocalTime.of(0, 0))) {
            days++;
            daysLabel.setText("Days: " + days);
        }

        // Update the time display
        updateGameTimeDisplay();

        // Change background and time of day at 8 PM and 7 AM
        if (gameTime.isAfter(LocalTime.of(20, 0)) || gameTime.isBefore(LocalTime.of(7, 0))) {
            gameBackground.setImage(nightBackground);
            timeDay.setText("Nighttime");
        } else {
            gameBackground.setImage(dayBackground);
            timeDay.setText("Daytime");
        }

        // Change side character at 7 AM
        if (gameTime.equals(LocalTime.of(7, 0))) {
            cycleSideCharacter();
        }
    }

    private void updateGameTimeDisplay() {
        timeCurrent.setText("Time: " + gameTime.format(TIME_FORMATTER));
    }

    /*
     * TODO: Supposed to be in the View package
     * */
    @FXML
    private void showMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Menu.fxml"));
            VBox settingsPane = fxmlLoader.load();

            // Create a new Stage for the Settings window
            Stage settingsStage = new Stage();
            settingsStage.setTitle("Settings");

            settingsStage.initModality(Modality.APPLICATION_MODAL); // Makes it a modal (popup) window
            settingsStage.setResizable(false);

            Scene scene = new Scene(settingsPane);
            settingsStage.setScene(scene);

            // Show the settings window
            settingsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
