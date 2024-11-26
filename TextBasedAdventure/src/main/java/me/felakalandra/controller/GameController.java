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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Getter;
import me.felakalandra.model.*;
import org.tinylog.Logger;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
    @Getter
    private MediaPlayer mediaPlayer;
    private LocalTime gameTime;
    private int days = 0;
    private boolean isMuted = false;

    // Game clock
    private Timeline gameClock;
    private boolean isGamePaused = false;

    // Initializing the main and side characters
    private Protagonist protagonist = new Protagonist();
    private Npc currentNpc;
    private EnemyNpc currentEnemyNpc;

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

    // List to store pending rewards
    private List<RewardTask> pendingRewards = new ArrayList<>();

    public GameController() {
        Media media = new Media(getClass().getResource("/Sounds/Gameplay_Sound.mp3").toExternalForm());
        mediaPlayer = new MediaPlayer(media);
    }

    @FXML
    private void initialize() {
        initializeGameState();
        startGameClock();  // Only required during initialize
        startGameMusic();
    }

    //Method that contains the common initialization logic
    private void initializeGameState() {
        // Set the background image to the AnchorPane
        gameBackground.fitWidthProperty().bind(gameBase.widthProperty());
        gameBackground.fitHeightProperty().bind(gameBase.heightProperty());

        questInfoInfo.setText("What to do:");
        questRewardInfo.setText("Rewards:");

        //Set default values
        protagonist = new Protagonist();
        days = 1;
        setUpperRow();

        // Set wallpaper and characters
        gameBackground.setImage(dayBackground);
        protagonistLeft.setImage(protagonistImageLevel1);

        // Load characters from JSON
        Npc.loadNpcs();
        EnemyNpc.loadEnemies();

        // Set the first NPC
        updateGameTimeDisplay();
        showOptionButtons();
        hideResponseArea();
        pendingRewards.clear();

        Logger.info("Game state initialized");

        advanceGameState();
    }

    private void startGameClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), event -> advanceTime()));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void advanceTime() {
        // If the game is paused, do not advance the time or update the game state
        if (isGamePaused) {
            return;
        }

        // Increase game time by X minute
        gameTime = gameTime.plusMinutes(60);

        // Check if a new day has begun
        if (gameTime.equals(LocalTime.of(0, 0))) {
            days++;
            daysLabel.setText("Days: " + days);
        }

        // Update the time display
        updateGameTimeDisplay();
        updateBackgroundAndTimeOfDay();

        // Set the option buttons visible at 7:00 AM and initiate a new NPC
        if (gameTime.equals(LocalTime.of(7, 0))) {
            showOptionButtons();
            fadeIn(npcsRight, 1);
            advanceGameState();
        }

        // Recieve rewards at 1:00 AM
        if (gameTime.equals(LocalTime.of(1, 0))) {
            hideOptionButtons();
            fadeOut(npcsRight, 1);
            applyPendingRewards();
        }

        // Hide the response area at 5:00 AM
        if (gameTime.equals(LocalTime.of(5, 0))) {
            hideResponseArea();
        }
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

    private void setUpperRow() {
        gameTime = INITIAL_GAME_TIME;
        hpLabel.setText("HP: " + protagonist.getHealth());
        goldLabel.setText("Gold: " + protagonist.getGold());
        damageLabel.setText("Damage: " + protagonist.getDamagePoints());
        daysLabel.setText("Days: " + days);
        levelLabel.setText("Level: " + protagonist.getLevel());
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

    private void showOptionButtons() {
        option1.setVisible(true);
        option2.setVisible(true);
        option3.setVisible(true);
    }

    private void hideOptionButtons() {
        option1.setVisible(false);
        option2.setVisible(false);
        option3.setVisible(false);
    }

    private void showResponse(String response) {
        // Set NPC's response
        npcResponseLabel.setText(response);
        responseArea.setVisible(true);
        responseArea.setManaged(true);

        // Fade transition for the NPC's response
        FadeTransition fadeLabel = new FadeTransition(Duration.seconds(1), npcResponseLabel);
        fadeLabel.setFromValue(0);
        fadeLabel.setToValue(1);

        FadeTransition fadeArea = new FadeTransition(Duration.seconds(1), responseArea);
        fadeArea.setFromValue(0);
        fadeArea.setToValue(1);

        fadeLabel.play();
        fadeArea.play();
    }

    private void hideResponseArea() {
        // Fade transition for hiding the NPC's response
        FadeTransition fadeOutLabel = new FadeTransition(Duration.seconds(1), npcResponseLabel);
        fadeOutLabel.setFromValue(1);
        fadeOutLabel.setToValue(0);

        FadeTransition fadeOutArea = new FadeTransition(Duration.seconds(1), responseArea);
        fadeOutArea.setFromValue(1);
        fadeOutArea.setToValue(0);

        fadeOutLabel.setOnFinished(event -> {
            // Hide the response area after fading out
            responseArea.setVisible(false);
            responseArea.setManaged(false);
            npcResponseLabel.setText("");
        });

        fadeOutLabel.play();
        fadeOutArea.play();
    }

    // Fade-out NPCS on the right side when they are leaving
    public static void fadeOut(ImageView imageView, double durationInSeconds) {
        if (imageView == null) return;

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(durationInSeconds), imageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> imageView.setVisible(false));
        fadeOut.play();
    }

    // Fade-in NPCS on the right side when they are arriving
    public static void fadeIn(ImageView imageView, double durationInSeconds) {
        if (imageView == null) return;

        imageView.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(durationInSeconds), imageView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void startGameMusic() {
        if (mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        }
    }

    public void stopGameMusic() {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.stop();
        }
    }

    public void toggleMute() {
        mediaPlayer.setVolume(isMuted ? 0.5 : 0.0);
        isMuted = !isMuted;
    }

    public void resetGame() {
        initializeGameState();
    }

    @FXML
    private void advanceGameState() {
        if (days < 10 || protagonist.getLevel() < 2) {
            npcRound();
        } else {
            if (new Random().nextDouble() < 0.8) {
                npcRound();
            } else {
                enemyRound();
            }
        }
    }

    private void npcRound() {
        setNpc(true);

        if (currentNpc != null && currentNpc.getDialogues() != null && !currentNpc.getDialogues().isEmpty()) {
            // Pick a random dialogue from the current NPC's dialogues
            Dialogue randomDialogue = getRandomDialogue(currentNpc.getDialogues());

            setDialogueDetails(randomDialogue, currentNpc.getName(), currentNpc.getReliability() + "%");

            Logger.info("Random dialogue selected: " + randomDialogue.getText());
        } else {
            questText.setText("No dialogue available.");
            Logger.warn("No dialogues available for NPC: " + currentNpc);
        }

        if (protagonist.isAlive()) {
            Logger.info("Protagonist is alive");
            levelUp();
            setUpperRow();
            updateGameTimeDisplay();
        }
    }

    private void enemyRound() {
        setNpc(false);

        if (currentEnemyNpc != null && currentEnemyNpc.getDialogues() != null && !currentEnemyNpc.getDialogues().isEmpty()) {
            // Pick a random dialogue from the current NPC's dialogues
            Dialogue randomDialogue = getRandomDialogue(currentEnemyNpc.getDialogues());

            setDialogueDetails(randomDialogue, currentEnemyNpc.getName(), currentEnemyNpc.getHealth() + "");

            Logger.info("Random dialogue selected: " + randomDialogue.getText());
        } else {
            questText.setText("No dialogue available.");
            Logger.warn("No dialogues available for NPC: " + currentNpc);
        }

        if (protagonist.isAlive()) {
            Logger.info("Protagonist is alive");
            levelUp();
            setUpperRow();
            updateGameTimeDisplay();
        }
    }

    private Dialogue getRandomDialogue(List<Dialogue> dialogues) {
        int randomIndex = (int) (Math.random() * dialogues.size());
        return dialogues.get(randomIndex);
    }

    // Set the current NPC's dialogues
    private void setDialogueDetails(Dialogue dialogue, String name, String reliability) {
        // Set the reward details
        rewardType1 = dialogue.getRewardType1();
        rewardType2 = dialogue.getRewardType2();
        number1 = numberGenerator(rewardType1);
        number2 = numberGenerator(rewardType2);
        questReward.setText(formattedReward());

        // Set the dialogue text
        questText.setText(replacePlaceholders(dialogue.getText(), number1, number2));
        questTextInfo.setText("What the " + name + " says:");
        // Set the quest type and reward details
        questType.setText(dialogue.getType().substring(0, 1).toUpperCase() + dialogue.getType().substring(1));
        questInfo.setText(replacePlaceholders(dialogue.getInfo(), number1, number2));
        // Set the reliability info
        questReliabilityInfo.setText("Should you trust the " + name + "?");
        questReliability.setText(name + "'s reliability is " + reliability);

        setOptionButtons(dialogue);
    }

    // Set the dialogue options for the buttons
    private void setOptionButtons(Dialogue dialogue) {
        if (dialogue.getOptions() != null) {
            if (dialogue.getOptions().containsKey("option1")) {
                option1.setText(dialogue.getOptions().get("option1").getText());
            }

            if (dialogue.getOptions().containsKey("option2")) {
                option2.setText(dialogue.getOptions().get("option2").getText());
            }

            if (dialogue.getOptions().containsKey("option3")) {
                option3.setText(dialogue.getOptions().get("option3").getText());
            }
        }
    }

    public void option1Button(ActionEvent actionEvent) {
        handleOptionButton("option1", true, true);
    }

    public void option2Button(ActionEvent actionEvent) {
        handleOptionButton("option2", false, true);
    }

    public void option3Button(ActionEvent actionEvent) {
        handleOptionButton("option3", false, false);
    }

    private void handleOptionButton(String optionKey, boolean questAccepted, boolean hideButtons) {
        DialogueOption option = currentNpc.getDialogues().get(0).getOptions().get(optionKey);
        if (option != null) {
            double npcReliability = currentNpc.getReliability();
            handleOptionSelection(option, rewardType1, number1, rewardType2, number2, questAccepted, npcReliability);
        }
        if (hideButtons) {
            hideOptionButtons();
        }
    }

    private void handleOptionSelection(DialogueOption option, String type1, int value1, String type2, int value2, boolean questAccepted, double npcReliability) {
        if (option != null) {
            showResponse(option.getResponse());

            if (questAccepted) {
                // Check if type1 is not "none" and has enough resources.
                if (!Objects.equals(type1, "none") && enoughResources(type1, value1)) {
                    // Deduct the required amount (value1) from type1.
                    addPendingReward(type1, -value1, npcReliability);

                    // Add the second reward/penalty (type2) as usual.
                    addPendingReward(type2, value2, npcReliability);
                } else if (Objects.equals(type1, "none")) {
                    // If type1 is "none", add the second reward/penalty (type2).
                    addPendingReward(type2, value2, npcReliability);
                }
            }
        } else {
            npcResponseLabel.setText("No response available.");
            resetResponseArea();
        }
    }

    private void resetResponseArea() {
        responseArea.setVisible(false);
        responseArea.setManaged(false);
        npcResponseLabel.setText("");
    }

    private void addPendingReward(String type, int value, double npcReliability) {
        boolean success = (Math.floor(Math.random() * 100) + 1) <= npcReliability;
        pendingRewards.add(new RewardTask(type, value, success));
    }

    private void applyPendingRewards() {
        // Collect feedback to show to the player
        StringBuilder feedback = new StringBuilder();

        if (pendingRewards.isEmpty()) {
            // No rewards were selected
            feedback.append("You were too hesitant to make a choice. The " + currentNpc.getName() + " left without giving you anything.");
        } else {
            for (RewardTask reward : pendingRewards) {
                if (reward.isSuccess()) {
                    statSetter(reward.getType(), reward.getValue());
                    feedback.append("You received ").append(reward.getValue()).append(" ").append(reward.getType()).append(".\n");
                } else {
                    feedback.append("The NPC failed to deliver ").append(reward.getValue()).append(" ").append(reward.getType()).append(".\n");
                }
            }
            pendingRewards.clear(); // Clear the list after applying all rewards
            updateProtagonistStats(); // Update protagonist stats in the UI
        }
        // Show feedback to the player
        showResponse(feedback.toString());
    }

    private void updateProtagonistStats() {
        hpLabel.setText("HP: " + protagonist.getHealth());
        goldLabel.setText("Gold: " + protagonist.getGold());
        damageLabel.setText("Damage: " + protagonist.getDamagePoints());
    }

    /*
     * TODO: Management for items
     * */
    // Modify a specific stat (e.g., gold, damage, or health) by a given value.
    private void statSetter(String stat, int number) {
        // Switch statement to handle different stats.
        switch (stat) {
            // Adjust the protagonist's gold by the specified value.
            case "gold" -> protagonist.setGold(protagonist.getGold() + number);
            // Adjust the protagonist's damage points by the specified value.
            case "damage" -> protagonist.setDamagePoints(protagonist.getDamagePoints() + number);
            // Adjust the protagonist's health by the specified value.
            case "hp" -> protagonist.setHealth(protagonist.getHealth() + number);
            // Debug message to print the stat being modified and the value used for the modification.
            default -> Logger.info("Unrecognized stat: " + stat);
        }
    }

    // Generate a random number within a range based on the specified stat.
    private int numberGenerator(String stat) {
        int min, max;
        switch (stat) {
            // Calculate the range as 10% to 30% of the protagonist's current gold.
            case "gold" -> {
                min = (int) (protagonist.getGold() * 0.2);
                max = (int) (protagonist.getGold() * 0.3);
                // Returns a random value within the range and weighted by the reliability of the character
                return (int) ((min + (int) (Math.random() * (max - min + 1))) * ((100 - currentNpc.getReliability()) * 0.1));
            }
            // Calculate the range as 10% to 30% of the protagonist's current damage points.
            case "damage" -> {
                min = (int) (protagonist.getDamagePoints() * 0.2);
                max = (int) (protagonist.getDamagePoints() * 0.3);
                // Returns a random value within the range and weighted by the reliability of the character
                return (int) ((min + (int) (Math.random() * (max - min + 1))) * ((100 - currentNpc.getReliability()) * 0.1));
            }
            case "hp" -> {
                // Calculate the range as 10% to 30% of the protagonist's current health.
                min = (int) (protagonist.getHealth() * 0.2);
                max = (int) (protagonist.getHealth() * 0.3);
                // Returns a random value within the range and weighted by the reliability of the character
                return (int) ((min + (int) (Math.random() * (max - min + 1))) * ((100 - currentNpc.getReliability()) * 0.1));
            }
        }
        return 0;
    }

    // Method to replace placeholders in a dialogue string with specific numeric values.
    public static String replacePlaceholders(String dialogue, int price1, int price2) {
        // Replace the placeholders {number1} and {number2} with the provided price1 and price2 values.
        return dialogue.replace("{number1}", String.valueOf(price1)).replace("{number2}", String.valueOf(price2));
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

    // Method to check if the protagonist has enough resources for a specific requirement.
    private boolean enoughResources(String stat, int number) {
        // Determine which stat to check based on the input string.
        switch (stat) {
            // Check if the protagonist's gold, damage, hp is greater than or equal to the required amount.
            case "gold" -> {
                return (protagonist.getGold() >= number);
            }
            case "damage" -> {
                return (protagonist.getDamagePoints() > number);
            }
            case "hp" -> {
                return (protagonist.getHealth() > number);
            }
            default -> {
                Logger.info("Unrecognized stat: " + stat);
                return false;
            }
        }
    }

    // Set's Npc-s up for the JavaFx
    @FXML
    public void setNpc(boolean isNpc) {
        if (isNpc) {
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
        } else {
            if (!EnemyNpc.getEnemies().isEmpty()) {
                boolean correspondingLevel = false;

                // Create a Random object to generate a random index
                Random random = new Random();

                while (!correspondingLevel) {
                    // Get a random index between 0 and Characters.getCharacters().size() - 1
                    int randomIndex = random.nextInt(EnemyNpc.getEnemies().size());

                    // Get the character at the random index
                    //currentNpc = Npc.getNpcs().get(randomIndex);
                    currentEnemyNpc = EnemyNpc.getEnemies().get(randomIndex);

                    if (currentEnemyNpc.getLevel() <= protagonist.getLevel()) {
                        correspondingLevel = true;
                    }
                }

                Logger.info("Selected EnemyNpc: " + currentEnemyNpc.getName() + " with " + currentEnemyNpc.getDialogues().size() + " dialogues");

                // Get the image path from the random character
                String imagePath = currentEnemyNpc.getPath();

                // Use the getImage method to load the image from the path
                Image characterImage = EnemyNpc.getImage(imagePath);

                // Assuming characterRight is an ImageView
                if (characterImage != null) {
                    npcsRight.setImage(characterImage);  // Set the image in the ImageView
                }
            } else {
                Logger.error("No characters available to display.");
            }
        }
    }

    @FXML
    private void showMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Menu.fxml"));
            VBox menuPane = fxmlLoader.load();

            // Accessing the MenuController instance
            MenuController menuController = fxmlLoader.getController();
            menuController.setGameController(this); // Transferring an existing controller to MenuController

            // Create a new Stage for the menu
            Stage menuStage = new Stage();
            menuStage.setTitle("Menu");

            menuStage.initModality(Modality.APPLICATION_MODAL);
            menuStage.initStyle(StageStyle.UNDECORATED);

            menuStage.setScene(new Scene(menuPane));
            menuStage.show();

            // Pause the game clock when the menu is opened
            pauseGameClock();

            // Resume the game clock when the menu is closed
            menuStage.setOnHidden(event -> resumeGameClock());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseGameClock() {
        if (gameClock != null) {
            gameClock.stop();
        }
        isGamePaused = true;
    }

    private void resumeGameClock() {
        if (gameClock != null) {
            gameClock.play();
        }
        isGamePaused = false;
    }

}
