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
import me.felakalandra.model.Npc;
import me.felakalandra.model.Protagonist;
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
    private static final String PROTAGONIST_PATH = "Images/Protagonist/Main1.png";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private AnchorPane gameBase;

    @FXML
    private ImageView gameBackground;

    @FXML
    private Button acceptButton;

    @FXML
    private Button declineButton;

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
    private Label mainText;

    @FXML
    public ImageView protagonistLeft;

    @FXML
    public ImageView npcsRight;

    private LocalTime gameTime;
    private int days = 0;

    // Background images
    private final Image dayBackground = new Image(DAY_BACKGROUND_PATH);
    private final Image nightBackground = new Image(NIGHT_BACKGROUND_PATH);
    private final Image dawnBackground = new Image(DAWN_BACKGROUND_PATH);

    // Main character image (always displayed on the left)
    private final Image protagonistImage = new Image(PROTAGONIST_PATH);

    // Initializing the main and side characters
    private Protagonist protagonist = new Protagonist();
    private Npc npc = new Npc();
    Npc currentNpc;


    //Method that contains the common initialization logic
    private void initializeGameState() {
        gameBase.setBackground(Background.fill(Color.LIGHTBLUE));

        //Player values reset
        protagonist = new Protagonist();

        //Set default values
        days = 0;
        setUpperRow();

        // Set wallpaper and characters
        gameBackground.setImage(dayBackground);
        protagonistLeft.setImage(protagonistImage);

        // Load characters from JSON
        Npc.loadNpcs();

        updateGameTimeDisplay();

        Logger.info("Game state initialized");

        advanceGameState();
    }

    @FXML
    private void advanceGameState() {
        // Set's Npc image in JavaFx
        setNpc();

        mainText.setText("Szia, uram! Egy aranyért dzsigoló kard?");

        // Check if the buttons are not null before setting their actions
        if (acceptButton != null) {
            acceptButton.setOnAction(event -> {
                mainText.setText("You have chosen to accept the offer!");
                Logger.info("Accept button was clicked");
                protagonist.setGold(protagonist.getGold() - 1);
                protagonist.setDamagePoints(protagonist.getDamagePoints() + 10);
            });
        }

        if (declineButton != null) {
            declineButton.setOnAction(event -> {
                mainText.setText("You have chosen to decline the offer.");
                Logger.info("Decline button was clicked");
            });
        }

        if (protagonist.isAlive()){
            Logger.info("Protagonist is alive");
            days = days + 1;
            setUpperRow();
            updateGameTimeDisplay();
        }
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

    /*
    TODO: When opening the menu, time supposed to stop
     */
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

    // Set's Npc-s up for the JavaFx
    @FXML
    public void setNpc(){
        if (!Npc.getNpcs().isEmpty()) {
            boolean correspondingLevel = false;

            // Create a Random object to generate a random index
            Random random = new Random();

            while (!correspondingLevel) {
                // Get a random index between 0 and Characters.getCharacters().size() - 1
                int randomIndex = random.nextInt(Npc.getNpcs().size());

                // Get the character at the random index
                currentNpc = Npc.getNpcs().get(randomIndex);

                if (currentNpc.getLevel() <= protagonist.getLevel()){
                    correspondingLevel = true;
                }
            }

            // Get the image path from the random character
            String imagePath = currentNpc.getPath();

            // Use the getImage method to load the image from the path
            Image characterImage = Npc.getImage(imagePath);

            // Assuming characterRight is an ImageView
            if (characterImage != null) {
                npcsRight.setImage(characterImage);  // Set the image in the ImageView
            }
        } else {
            Logger.error("No characters available to display.");
        }
    }


    public void setUpperRow(){
        gameTime = INITIAL_GAME_TIME;
        hpLabel.setText("HP: " + protagonist.getHeartPoints());
        goldLabel.setText("Gold: " + protagonist.getGold());
        damageLabel.setText("Damage: " + protagonist.getDamagePoints());
        daysLabel.setText("Days: " + days);
    }
}
