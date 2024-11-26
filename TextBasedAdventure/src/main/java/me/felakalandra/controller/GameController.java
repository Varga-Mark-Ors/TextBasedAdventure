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

    //Initializing the values needed for the character process
    private int number1;
    private int number2;
    private String rewardType1;
    private String rewardType2;

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
        showOptionButtons();
        hideResponseArea();

        Logger.info("Game state initialized");

        advanceGameState();
    }

    private void hideOptionButtons() {
        option1.setVisible(false);
        option2.setVisible(false);
        option3.setVisible(false);
    }

    public void option1Button(ActionEvent actionEvent) {
        DialogueOption option = currentNpc.getDialogues().get(0).getOptions().get("option1");
        if (option != null) {
            handleOptionSelection(option, rewardType1, number1, rewardType2, number2, true);
        }
        hideOptionButtons();
    }

    public void option2Button(ActionEvent actionEvent) {
        DialogueOption option = currentNpc.getDialogues().get(0).getOptions().get("option2");
        if (option != null) {
            handleOptionSelection(option, rewardType1, number1, rewardType2, number2, false);
        }
        hideOptionButtons();
    }

    public void option3Button(ActionEvent actionEvent) {
        DialogueOption option = currentNpc.getDialogues().get(0).getOptions().get("option3");
        if (option != null) {
            handleOptionSelection(option, rewardType1, number1, rewardType2, number2, false);
        }
    }

    // Updated handleOptionSelection to include rewards and penalties
    private void handleOptionSelection(DialogueOption option, String type1, int value1, String type2, int value2, boolean questAccepted) {
        if (option != null) {
            // Show NPC response
            showResponse(option.getResponse());

            // If the player accepts a quest or trade
            if (questAccepted) {
                if (!Objects.equals(type1, "none")) {
                    statSetter(type1, -value1); // Deduct cost (e.g., gold, HP)
                }
                statSetter(type2, value2); // Grant reward (e.g., gold, HP, damage)
            }
        } else {
            npcResponseLabel.setText("No response available.");
            resetResponseArea();
        }

        // Update the protagonist stats UI
        updateProtagonistStats();
    }

    private void resetResponseArea() {
        responseArea.setVisible(false);
        responseArea.setManaged(false);
        npcResponseLabel.setText(""); // Clear any previous response
    }

    private void showResponse(String response) {
        // Set NPC's response
        npcResponseLabel.setText(response);

        // Ensure response area is visible
        responseArea.setVisible(true);
        responseArea.setManaged(true);

        // Fade transition for npcResponseLabel
        FadeTransition fadeLabel = new FadeTransition(Duration.seconds(1), npcResponseLabel);
        fadeLabel.setFromValue(0);
        fadeLabel.setToValue(1);

        // Fade transition for responseArea
        FadeTransition fadeArea = new FadeTransition(Duration.seconds(1), responseArea);
        fadeArea.setFromValue(0);
        fadeArea.setToValue(1);

        // Play both transitions
        fadeLabel.play();
        fadeArea.play();
    }

    private void hideResponseArea() {
        // Fade out transition for npcResponseLabel
        FadeTransition fadeOutLabel = new FadeTransition(Duration.seconds(1), npcResponseLabel);
        fadeOutLabel.setFromValue(1); // Start fully visible
        fadeOutLabel.setToValue(0);   // Fade to fully transparent

        // Fade out transition for responseArea
        FadeTransition fadeOutArea = new FadeTransition(Duration.seconds(1), responseArea);
        fadeOutArea.setFromValue(1); // Start fully visible
        fadeOutArea.setToValue(0);   // Fade to fully transparent

        // Set the onFinished event for fadeOutLabel
        fadeOutLabel.setOnFinished(event -> {
            // Hide the response area after fading out
            responseArea.setVisible(false);
            responseArea.setManaged(false);
            npcResponseLabel.setText(""); // Clear text
        });

        // Play both fade-out transitions
        fadeOutLabel.play();
        fadeOutArea.play();
    }

    @FXML
    private void advanceGameState() {
        // Set NPC image in JavaFx
        setNpc();

        if (currentNpc != null && currentNpc.getDialogues() != null && !currentNpc.getDialogues().isEmpty()) {
            // Pick a random dialogue from the current NPC's dialogues
            int randomIndex = (int) (Math.random() * currentNpc.getDialogues().size());
            Dialogue randomDialogue = currentNpc.getDialogues().get(randomIndex);

            // Set the reward details
            rewardType1 = randomDialogue.getRewardType1();
            rewardType2 = randomDialogue.getRewardType2();
            number1 = numberGenerator(rewardType1);
            number2 = numberGenerator(rewardType2);
            questReward.setText(rewardType1 + rewardType2);
            questReward.setText(formattedReward());

            // Set the dialogue text
            questText.setText(replacePlaceholders(randomDialogue.getText(), number1, number2));
            questTextInfo.setText("What the " + currentNpc.getName() + " says:");

            // Set the quest type and reward details
            questType.setText(randomDialogue.getType().substring(0, 1).toUpperCase() + randomDialogue.getType().substring(1));
            questInfo.setText(replacePlaceholders(randomDialogue.getInfo(), number1, number2));

            // Set the reliability info
            questReliabilityInfo.setText("Should you trust the " + currentNpc.getName() + "?");
            questReliability.setText(currentNpc.getName() + "'s reliability is " + currentNpc.getReliability() + "%");

            // Set the dialogue options for the buttons
            if (randomDialogue.getOptions() != null) {
                if (randomDialogue.getOptions().containsKey("option1")) {
                    option1.setText(randomDialogue.getOptions().get("option1").getText());
                    if (!Objects.equals(rewardType1, "none")){
                        statSetter(rewardType1, number1 * - 1);
                    }
                    if (Npc.reliable(currentNpc)){
                        statSetter(rewardType2, number2);
                    }
                }

                if (randomDialogue.getOptions().containsKey("option2")) {
                    option2.setText(randomDialogue.getOptions().get("option2").getText());
                }

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
            levelUp();
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
        // Update every 1 second instead of 0.1 seconds
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), event -> advanceTime()));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    /*
    TODO: When opening the menu, time supposed to stop
     */
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

    private void showOptionButtons() {
        option1.setVisible(true);
        option2.setVisible(true);
        option3.setVisible(true);
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
        levelLabel.setText("Level: " + protagonist.getLevel());
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

    private void levelUp() {
        if (((protagonist.getHealth() + protagonist.getDamagePoints()) > 200) && (protagonist.getLevel() < 2)) {
            protagonistLeft.setImage(protagonistImageLevel2);
            protagonist.setLevel(2);
        }

        if (((protagonist.getHealth() + protagonist.getDamagePoints()) > 300) && (protagonist.getLevel() < 3)) {
            protagonistLeft.setImage(protagonistImageLevel3);
            protagonist.setLevel(3);
        }
    }

    /*
     * TODO: Management for items
     * */
    // Method to modify a specific stat (e.g., gold, damage, or health) by a given value.
    private void statSetter(String stat, int number) {
        // Debug message to print the stat being modified and the value used for the modification.
        System.out.println("Stat: " + stat + ", Number: " + number);

        // Switch statement to handle different stats.
        switch (stat) {
            case "gold" ->
                // Adjust the protagonist's gold by the specified value.
                    protagonist.setGold(protagonist.getGold() + number);
            case "damage" ->
                // Adjust the protagonist's damage points by the specified value.
                    protagonist.setDamagePoints(protagonist.getDamagePoints() + number);
            case "hp" ->
                // Adjust the protagonist's health by the specified value.
                    protagonist.setHealth(protagonist.getHealth() + number);
            default -> {
                // If the provided stat is unrecognized, print an error message.
                System.out.println("Unrecognized stat: " + stat);
            }
        }
    }

    // Method to generate a random number within a range based on the specified stat.
    private int numberGenerator(String stat) {
        switch (stat) {
            case "gold" -> {
                // Calculate the range as 10% to 30% of the protagonist's current gold.
                int min = (int) (protagonist.getGold() * 0.2);
                int max = (int) (protagonist.getGold() * 0.3);
                // Returns a random value within the range and weighted by the reliability of the character
                return (int) ((min + (int) (Math.random() * (max - min + 1)))
                        * ((100 - currentNpc.getReliability()) * 0.1));
            }
            case "damage" -> {
                // Calculate the range as 10% to 30% of the protagonist's current damage points.
                int min = (int) (protagonist.getDamagePoints() * 0.2);
                int max = (int) (protagonist.getDamagePoints() * 0.3);
                // Returns a random value within the range and weighted by the reliability of the character
                return (int) ((min + (int) (Math.random() * (max - min + 1)))
                        * ((100 - currentNpc.getReliability()) * 0.1));
            }
            case "hp" -> {
                // Calculate the range as 10% to 30% of the protagonist's current health.
                int min = (int) (protagonist.getHealth() * 0.2);
                int max = (int) (protagonist.getHealth() * 0.3);
                // Returns a random value within the range and weighted by the reliability of the character
                return (int) ((min + (int) (Math.random() * (max - min + 1)))
                        * ((100 - currentNpc.getReliability()) * 0.1));
            }
        }
        // Default return value if the stat does not match any case.
        return 0;
    }

    // Method to replace placeholders in a dialogue string with specific numeric values.
    public static String replacePlaceholders(String dialogue, int price1, int price2) {
        // Replace the placeholders {number1} and {number2} with the provided price1 and price2 values.
        return dialogue.replace("{number1}", String.valueOf(price1))
                .replace("{number2}", String.valueOf(price2));
    }

    // Method to generate a formatted string describing the rewards and costs.
    private String formattedReward() {
        // If the first reward type is "none", return a simplified message about the second reward.
        if (Objects.equals(rewardType1, "none")) {
            return "You will get: " + number2 + " " + rewardType2;
        }
        // Otherwise, include both the cost and the reward in the message.
        return "You will give: " + number1 + " " + rewardType1 + ". You will get: " + number2 + " " + rewardType2;
    }
}