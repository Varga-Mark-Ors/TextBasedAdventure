package me.felakalandra.controller;

import javafx.animation.FadeTransition;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import me.felakalandra.model.Dialogue;
import me.felakalandra.model.DialogueOption;
import me.felakalandra.model.Npc;
import me.felakalandra.model.Protagonist;
import org.tinylog.Logger;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;

public class GameController {
    private static final LocalTime INITIAL_GAME_TIME = LocalTime.of(7, 0);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private AnchorPane gameBase;
    @FXML
    private ImageView gameBackground, protagonistLeft, npcsRight;
    @FXML
    private Button option1, option2, option3;
    @FXML
    private Label daysLabel, timeCurrent, timeDay, hpLabel, goldLabel, damageLabel, levelLabel, questText, questType, questInfo, questReward, questReliability, questInfoInfo, questTextInfo, questRewardInfo, questReliabilityInfo;
    @FXML
    private StackPane responseArea;
    @FXML
    private Label npcResponseLabel;

    private LocalTime gameTime;
    private int days = 0;

    // Initializing the main and side characters
    private Protagonist protagonist = new Protagonist();
    private Npc currentNpc;

    // Background images
    private final Image dayBackground = new Image("Images/Background/daytime.jpg");
    private final Image nightBackground = new Image("Images/Background/night.jpg");
    private final Image dawnBackground = new Image("Images/Background/dawn.jpg");

    // Main character's images (always displayed on the left) at level 1
    private final Image protagonistImageLevel1 = new Image("Images/Protagonist/Main1.png");
    private final Image protagonistImageLevel2 = new Image("Images/Protagonist/Main2.png");
    private final Image protagonistImageLevel3 = new Image("Images/Protagonist/Main3.png");

    public void resetGame() {
        initializeGameState();
        Logger.info("Game has been reset");
    }

    @FXML
    private void initialize() {
        initializeGameState();
        startGameClock(); // Only required during initialize
    }

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
        protagonistLeft.setImage(protagonistImageLevel1);

        // Load characters from JSON
        Npc.loadNpcs();
        updateGameTimeDisplay();
        advanceGameState();

        Logger.info("Game state initialized");
    }

    // Start the game clock
    private void startGameClock() {
        // 10 IRL seconds for each in-game minute
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> advanceTime()));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void advanceTime() {
        // Increase game time by X minute
        gameTime = gameTime.plusMinutes(5);

        // Check if a new day has begun
        if (gameTime.equals(LocalTime.of(0, 0))) {
            days++;
            daysLabel.setText("Days: " + days);
        }

        // Update the time display
        updateGameTimeDisplay();
        updateBackgroundAndTimeOfDay();

        // Set the option buttons visible at 7:00 AM
        if (gameTime.equals(LocalTime.of(7, 0))) showOptionButtons();
        // Hide the response area at 5:00 AM
        if (gameTime.equals(LocalTime.of(5, 0))) hideResponseArea();

    }
    // Change background and time of day at 8 PM and 7 AM
    private void updateBackgroundAndTimeOfDay() {
        // Night: from 20:00 to 4:00
        if (gameTime.isAfter(LocalTime.of(20, 0)) || gameTime.isBefore(LocalTime.of(4, 0))) {
            gameBackground.setImage(nightBackground);
            timeDay.setText("Nighttime");
            // Dawn: from 4:00 to 8:00
        } else if (gameTime.isAfter(LocalTime.of(4, 0)) && gameTime.isBefore(LocalTime.of(8, 0))) {
            gameBackground.setImage(dawnBackground);
            timeDay.setText("Dawn");
            // Daytime: from 8:00 to 20:00
        } else {
            gameBackground.setImage(dayBackground);
            timeDay.setText("Daytime");
        }
    }

    private void updateGameTimeDisplay() {
        timeCurrent.setText("Time: " + gameTime.format(TIME_FORMATTER));
    }

    // Set the upper row of the game screen
    private void setUpperRow() {
        gameTime = INITIAL_GAME_TIME;
        hpLabel.setText("HP: " + protagonist.getHealth());
        goldLabel.setText("Gold: " + protagonist.getGold());
        damageLabel.setText("Damage: " + protagonist.getDamagePoints());
        daysLabel.setText("Days: " + days);
        levelLabel.setText("Level: " + protagonist.getLevel());
    }

    // Show the option buttons
    private void showOptionButtons() {
        option1.setVisible(true);
        option2.setVisible(true);
        option3.setVisible(true);
    }

    // Hide the option buttons
    private void hideOptionButtons() {
        option1.setVisible(false);
        option2.setVisible(false);
        option3.setVisible(false);
    }

    public void option1Button(ActionEvent actionEvent) {
        handleOptionSelection(currentNpc.getDialogues().get(0).getOptions().get("option1"));
        hideOptionButtons();
    }

    public void option2Button(ActionEvent actionEvent) {
        handleOptionSelection(currentNpc.getDialogues().get(0).getOptions().get("option2"));
        hideOptionButtons();
    }

    public void option3Button(ActionEvent actionEvent) {
        handleOptionSelection(currentNpc.getDialogues().get(0).getOptions().get("option3"));
        hideOptionButtons();
    }

    // Handle the selected option
    private void handleOptionSelection(DialogueOption option) {
        if (option != null) {
            showResponse(option.getResponse());
        } else {
            npcResponseLabel.setText("No response available.");
            resetResponseArea();
        }
    }

    // Show the NPC's response
    private void showResponse(String response) {
        npcResponseLabel.setText(response);
        responseArea.setVisible(true);
        responseArea.setManaged(true);

        playFadeTransition(npcResponseLabel, 0, 1);
        playFadeTransition(responseArea, 0, 1);
    }

    // Hide the NPC's response
    private void hideResponseArea() {
        playFadeTransition(npcResponseLabel, 1, 0).setOnFinished(event -> resetResponseArea());
        playFadeTransition(responseArea, 1, 0);
    }

    // Reset the response area
    private void resetResponseArea() {
        responseArea.setVisible(false);
        responseArea.setManaged(false);
        npcResponseLabel.setText("");
    }

    // Play a fade transition
    private FadeTransition playFadeTransition(javafx.scene.Node node, double fromValue, double toValue) {
        // Create a fade transition for the specified node
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), node);

        // Set the initial and final opacity values
        fadeTransition.setFromValue(fromValue);
        fadeTransition.setToValue(toValue);

        fadeTransition.play();
        return fadeTransition;
    }

    // Advance the game state
    @FXML
    private void advanceGameState() {
        // Set NPC image in JavaFx
        setNpc();
        if (currentNpc != null && currentNpc.getDialogues() != null && !currentNpc.getDialogues().isEmpty()) {
            // Pick a random dialogue from the current NPC's dialogues
            Dialogue randomDialogue = currentNpc.getDialogues().get(new Random().nextInt(currentNpc.getDialogues().size()));
            setDialogue(randomDialogue);
            Logger.info("Random dialogue selected: " + randomDialogue.getText());
        } else {
            questText.setText("No dialogue available.");
            Logger.warn("No dialogues available for NPC: " + currentNpc);
        }
        if (protagonist.isAlive()) {
            Logger.info("Protagonist is alive");
            days++;
            levelUp();
            setUpperRow();
            updateGameTimeDisplay();
        }
    }

    // Set the reward, dialogue, type, info, and reliability
    private void setDialogue(Dialogue randomDialogue) {

        // Set the quest's reward
        questReward.setText(formattedReward(randomDialogue));
        questText.setText(replacePlaceholders(randomDialogue.getText(), numberGenerator(randomDialogue.getRewardType1()),
                numberGenerator(randomDialogue.getRewardType2())));

        // Set the NPC's dialogue
        questTextInfo.setText("What the " + currentNpc.getName() + " says:");
        questType.setText(capitalizeFirstLetter(randomDialogue.getType()));
        questInfo.setText(replacePlaceholders(randomDialogue.getInfo(), numberGenerator(randomDialogue.getRewardType1()),
                numberGenerator(randomDialogue.getRewardType2())));

        // Set the reliability and the reliability question
        questReliabilityInfo.setText("Should you trust the " + currentNpc.getName() + "?");
        questReliability.setText(currentNpc.getName() + "'s reliability is " + currentNpc.getReliability() + "%");
        setOptionButtons(randomDialogue);
    }

    // Set the option buttons labels text
    private void setOptionButtons(Dialogue randomDialogue) {
        if (randomDialogue.getOptions() != null) {
            if (randomDialogue.getOptions().containsKey("option1")) {
                option1.setText(randomDialogue.getOptions().get("option1").getText());
            }
            if (randomDialogue.getOptions().containsKey("option2")) {
                option2.setText(randomDialogue.getOptions().get("option2").getText());
            }
            if (randomDialogue.getOptions().containsKey("option3")) {
                option3.setText(randomDialogue.getOptions().get("option3").getText());
            }
        }
    }

    // Set's Npc-s up for the JavaFx
    private void setNpc() {
        if (!Npc.getNpcs().isEmpty()) {
            currentNpc = Npc.getNpcs().stream().filter(npc -> npc.getLevel() <= protagonist.getLevel()).findAny().orElse(null);
            if (currentNpc != null) {
                npcsRight.setImage(Npc.getImage(currentNpc.getPath()));
                Logger.info("Selected NPC: " + currentNpc.getName() + " with " + currentNpc.getDialogues().size() + " dialogues");
            }
        } else {
            Logger.error("No characters available to display.");
        }
    }

    private void levelUp() {
        if (protagonist.getHealth() + protagonist.getDamagePoints() > 200 && protagonist.getLevel() < 2) {
            protagonistLeft.setImage(protagonistImageLevel2);
            protagonist.setLevel(2);
        }
        if (protagonist.getHealth() + protagonist.getDamagePoints() > 300 && protagonist.getLevel() < 3) {
            protagonistLeft.setImage(protagonistImageLevel3);
            protagonist.setLevel(3);
        }
    }

    // Method to generate a random number within a range based on the specified stat.
    private int numberGenerator(String stat) {
        int min, max;
        switch (stat) {
            case "gold" -> {
                // Calculate the range as 10% to 30% of the protagonist's current gold.
                min = (int) (protagonist.getGold() * 0.2);
                max = (int) (protagonist.getGold() * 0.3);
            }
            case "damage" -> {
                // Calculate the range as 10% to 30% of the protagonist's current damage points.
                min = (int) (protagonist.getDamagePoints() * 0.2);
                max = (int) (protagonist.getDamagePoints() * 0.3);
            }
            case "hp" -> {
                // Calculate the range as 10% to 30% of the protagonist's current health.
                min = (int) (protagonist.getHealth() * 0.2);
                max = (int) (protagonist.getHealth() * 0.3);
            }
            default -> {
                return 0;
            }
        }
        // Return a random number within the specified range, adjusted by the NPC's reliability.
        return (int) ((min + (int) (Math.random() * (max - min + 1))) * ((100 - currentNpc.getReliability()) * 0.1));
    }

    // Method to replace placeholders in a dialogue string with specific numeric values.
    public static String replacePlaceholders(String dialogue, int price1, int price2) {
        // Replace the placeholders {number1} and {number2} with the provided price1 and price2 values.
        return dialogue.replace("{number1}", String.valueOf(price1))
                .replace("{number2}", String.valueOf(price2));
    }

    // Method to generate a formatted string describing the rewards and costs.
    private String formattedReward(Dialogue randomDialogue) {
        // Get the reward types and generate random numbers for each.
        String rewardType1 = randomDialogue.getRewardType1();
        String rewardType2 = randomDialogue.getRewardType2();
        int number1 = numberGenerator(rewardType1);
        int number2 = numberGenerator(rewardType2);

        if (Objects.equals(rewardType1, "none")) {
            return "You will get: " + number2 + " " + rewardType2;
        }
        // Otherwise, include both the cost and the reward in the message.
        return "You will give: " + number1 + " " + rewardType1 + ". You will get: " + number2 + " " + rewardType2;
    }

    // Method to capitalize the first letter of a string.
    private String capitalizeFirstLetter(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

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
            menuStage.setTitle("Menu");
            menuStage.initModality(Modality.APPLICATION_MODAL);
            menuStage.initStyle(StageStyle.UNDECORATED);
            menuStage.setScene(new Scene(menuPane));
            menuStage.show();
        } catch (IOException e) {
            Logger.error(e);
        }
    }
}