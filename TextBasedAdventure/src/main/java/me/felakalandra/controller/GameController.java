package me.felakalandra.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
import me.felakalandra.model.Characters;
import me.felakalandra.model.Player;
import org.tinylog.Logger;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class GameController {
    private static final LocalTime INITIAL_GAME_TIME = LocalTime.of(7, 0);
    private static final String DAY_BACKGROUND_PATH = "Images/Background/daytime.jpg";
    private static final String NIGHT_BACKGROUND_PATH = "Images/Background/night.jpg";
    private static final String DAWN_BACKGROUND_PATH = "Images/Background/dawn.jpg";
    private static final String MAIN_CHARACTER_PATH = "Images/Protagonist/Main1.png";
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
    private Label hpLabel;

    @FXML
    private Label goldLabel;

    @FXML
    private Label damageLabel;

    @FXML
    public ImageView characterLeft;

    @FXML
    public ImageView characterRight;

    private LocalTime gameTime;
    private int days = 0;

    // Background images
    private final Image dayBackground = new Image(DAY_BACKGROUND_PATH);
    private final Image nightBackground = new Image(NIGHT_BACKGROUND_PATH);
    private final Image dawnBackground = new Image(DAWN_BACKGROUND_PATH);

    // Main character image (always displayed on the left)
    private final Image mainCharacter = new Image(MAIN_CHARACTER_PATH);

    // Initializing the main and side characters
    private Characters characters = new Characters();
    private Player player = new Player();

    //Method that contains the common initialization logic
    private void initializeGameState() {
        gameBase.setBackground(Background.fill(Color.LIGHTBLUE));

        //Player values reset
        player = new Player();

        //Set default values
        days = 0;
        gameTime = INITIAL_GAME_TIME;
        hpLabel.setText("HP: " + player.getHeartPoints());
        goldLabel.setText("Gold: " + player.getGold());
        damageLabel.setText("Damage: " + player.getDamagePoints());
        daysLabel.setText("Days: " + days);

        // Set wallpaper and characters
        gameBackground.setImage(dayBackground);
        characterLeft.setImage(mainCharacter);

        // Load characters from JSON
        Characters.loadCharacters();

        if (!Characters.getCharacters().isEmpty()) {
            // Create a Random object to generate a random index
            Random random = new Random();

            // Get a random index between 0 and Characters.getCharacters().size() - 1
            int randomIndex = random.nextInt(Characters.getCharacters().size());

            // Get the character at the random index
            Characters randomCharacter = Characters.getCharacters().get(randomIndex);

            // Get the image path from the random character
            String imagePath = randomCharacter.getPath();

            // Use the getImage method to load the image from the path
            Image characterImage = Characters.getImage(imagePath);

            // Assuming characterRight is an ImageView
            if (characterImage != null) {
                characterRight.setImage(characterImage);  // Set the image in the ImageView
            }
        } else {
            Logger.error("No characters available to display.");
        }

        updateGameTimeDisplay();

        Logger.info("Game state initialized");
    }

    @FXML
    private void initialize() {
        initializeGameState();
        startGameClock(); // Only required during initialize
    }

    public void resetGame() {
        initializeGameState();
    }


    // Start the game clock
    private void startGameClock() {
        // 10 IRL seconds for each in-game minute
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> advanceTime()));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
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
        if (gameTime.isAfter(LocalTime.of(20, 0)) || gameTime.isBefore(LocalTime.of(4, 0))) {
            // Night: from 20:00 to 4:00
            gameBackground.setImage(nightBackground);
            timeDay.setText("Nighttime");
        } else if (gameTime.isAfter(LocalTime.of(4, 0)) && gameTime.isBefore(LocalTime.of(8, 0))) {
            // Dawn: from 4:00 to 8:00
            gameBackground.setImage(dawnBackground);
            timeDay.setText("Dawn");
        } else {
            // Daytime: from 8:00 to 20:00
            gameBackground.setImage(dayBackground);
            timeDay.setText("Daytime");
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
            VBox menuPane = fxmlLoader.load();

            // Accessing the MenuController instance
            MenuController menuController = fxmlLoader.getController();
            menuController.setGameController(this); //Transferring an existing controller to MenuController

            // Create a new Stage for the menu
            Stage menuStage = new Stage();
            menuStage.setTitle("Menü");
            menuStage.initModality(Modality.APPLICATION_MODAL);
            menuStage.setScene(new Scene(menuPane));
            menuStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
