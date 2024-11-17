package me.felakalandra.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import me.felakalandra.model.Dialogue;
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
    private Label questText;

    @FXML
    private Label questType;

    @FXML
    private Label questInfo;

    @FXML
    private Label questReward;

    @FXML
    private Label questReliability;

    @FXML
    public Label questInfoInfo;

    @FXML
    public Label questTextInfo;

    @FXML
    public Label questRewardInfo;

    @FXML
    private Label questReliabilityInfo;

    @FXML
    public Button option1;

    @FXML
    public Button option2;

    @FXML
    public Button option3;

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
        // Set the background image to the AnchorPane
        gameBackground.fitWidthProperty().bind(gameBase.widthProperty());
        gameBackground.fitHeightProperty().bind(gameBase.heightProperty());

        questInfoInfo.setText("What to do:");
        questRewardInfo.setText("Rewards:");

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

    public void option1Button(ActionEvent actionEvent) {
        questText.setText("Option 1 selected");
    }

    public void option2Button(ActionEvent actionEvent) {
        questText.setText("Option 2 selected");
    }

    public void option3Button(ActionEvent actionEvent) {
        questText.setText("Option 3 selected");
    }

    @FXML
    private void advanceGameState() {
        // Set NPC image in JavaFx
        setNpc();

        if (currentNpc != null && currentNpc.getDialogues() != null && !currentNpc.getDialogues().isEmpty()) {
            // Pick a random dialogue from the current NPC's dialogues
            int randomIndex = (int) (Math.random() * currentNpc.getDialogues().size());
            Dialogue randomDialogue = currentNpc.getDialogues().get(randomIndex);

            // Set the text of the dialogue
            questText.setText(randomDialogue.getText());
            questTextInfo.setText("What the " + currentNpc.getName() + " says:");

            // Set the quest type and reward details
            questType.setText(randomDialogue.getType().substring(0, 1).toUpperCase() + randomDialogue.getType().substring(1));
            questInfo.setText(randomDialogue.getInfo());
            questReward.setText(randomDialogue.getReward().toString());

            questReliabilityInfo.setText("Should you trust the " + currentNpc.getName() + "?");
            questReliability.setText(currentNpc.getName() + "'s reliability is " + currentNpc.getReliability() + "%");

            // Now, set the options for buttons based on the options map
            if (randomDialogue.getOptions() != null) {
                // Option 1
                if (randomDialogue.getOptions().containsKey("option1")) {
                    option1.setText(randomDialogue.getOptions().get("option1").getText());
                }

                // Option 2
                if (randomDialogue.getOptions().containsKey("option2")) {
                    option2.setText(randomDialogue.getOptions().get("option2").getText());
                }

                // Option 3
                if (randomDialogue.getOptions().containsKey("option3")) {
                    option3.setText(randomDialogue.getOptions().get("option3").getText());
                }
            }

            Logger.info("Random dialogue selected: " + randomDialogue.getText());
        } else {
            questText.setText("No dialogue available.");
            Logger.warn("No dialogues available for NPC: " + currentNpc);
        }

        if (protagonist.isAlive()) {
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

    // Set's Npc-s up for the JavaFx
    @FXML
    public void setNpc() {
        if (!Npc.getNpcs().isEmpty()) {
            boolean correspondingLevel = false;

            // Create a Random object to generate a random index
            Random random = new Random();

            while (!correspondingLevel) {
                // Get a random index between 0 and Characters.getCharacters().size() - 1
                int randomIndex = random.nextInt(Npc.getNpcs().size());

                // Get the character at the random index
                currentNpc = Npc.getNpcs().get(randomIndex);

                if (currentNpc.getLevel() <= protagonist.getLevel()) {
                    correspondingLevel = true;
                }
            }

            // Log NPC name and dialogue count for debugging
            Logger.info("Selected NPC: " + currentNpc.getName() + " with " + currentNpc.getDialogues().size() + " dialogues");

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


    public void updateProtagonistStats() {
        hpLabel.setText("HP: " + protagonist.getHealth());
        goldLabel.setText("Gold: " + protagonist.getGold());
        damageLabel.setText("Damage: " + protagonist.getDamagePoints());
    }

    public void setUpperRow() {
        gameTime = INITIAL_GAME_TIME;
        hpLabel.setText("HP: " + protagonist.getHealth());
        goldLabel.setText("Gold: " + protagonist.getGold());
        damageLabel.setText("Damage: " + protagonist.getDamagePoints());
        daysLabel.setText("Days: " + days);
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
            menuStage.initStyle(StageStyle.UNDECORATED);

            menuStage.setScene(new Scene(menuPane));
            menuStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
