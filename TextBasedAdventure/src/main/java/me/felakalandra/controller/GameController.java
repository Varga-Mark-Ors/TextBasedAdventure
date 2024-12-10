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
import me.felakalandra.services.*;
import me.felakalandra.util.DialogueUtils;
import me.felakalandra.util.GameUtils;
import me.felakalandra.util.OptionUtils;
import me.felakalandra.util.TimeUtils;
import org.tinylog.Logger;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Random;

public class GameController {
    private static final LocalTime INITIAL_GAME_TIME = LocalTime.of(7, 0);

    @FXML
    private AnchorPane gameBase;
    @FXML
    private ImageView gameBackground, protagonistLeft, npcsRight, objectiveImage1, objectiveImage2, objectiveImage3;
    @FXML
    private Button option1, option2, option3;
    @FXML
    private Label scoreLabel, daysLabel, timeCurrent, timeDay, hpLabel, goldLabel, damageLabel, levelLabel, questText, questType, questInfo, questReward, questReliability, questInfoInfo, questTextInfo, questRewardInfo, questReliabilityInfo, objectiveLabel2Counter;
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

    // Main character's images (always displayed on the left) at level 1
    private final Image protagonistImageLevel1 = new Image("Images/Protagonist/Main1.png");

    private int dailyEncounterCount = 0;
    private static final int MAX_DAILY_ENCOUNTERS = 3; // Maximális találkozások száma egy nap alatt

    // Initializing the score
    private int score = 0;

    //Initializing the values needed for the character process
    private int number1;
    private int number2;
    private String rewardType1;
    private String rewardType2;

    private final RewardService rewardService = new RewardService();
    private final NpcService npcService = new NpcService();
    private final GameLogicService gameLogicService = new GameLogicService();
    private final GameUtils gameUtils = new GameUtils();
    private final DialogueService dialogueService = new DialogueService();
    private ObjectiveService objectiveService;

    private final OptionUtils optionUtils = new OptionUtils();
    private final DialogueUtils dialogueUtils = new DialogueUtils();
    private final TimeUtils timeUtils = new TimeUtils();

    private double enemyChance = 0.2; // Kezdeti esély az enemyRound-ra
    private boolean lastRoundWasEnemy = false; // Nyomon követi, hogy az előző kör enemyRound volt-e
    private final double enemyHpScale = 0.15;
    private final int multiplierAfterXDays = 10;

    public GameController() {
        Media media = new Media(getClass().getResource("/Sounds/Gameplay_Sound.mp3").toExternalForm());
        mediaPlayer = new MediaPlayer(media);
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

        // Set the initial text for quest-related labels
        questInfoInfo.setText("What to do:");
        questRewardInfo.setText("Rewards:");

        //Set default values
        protagonist = new Protagonist();
        days = 1;
        gameTime = INITIAL_GAME_TIME;
        dailyEncounterCount = 0; // Reset the daily encounter count

        // Initialize UI labels with default protagonist stats
        gameUtils.setInitialLabels(hpLabel, goldLabel, damageLabel, daysLabel, levelLabel, protagonist, days);

        // Set wallpaper and characters
        gameBackground.setImage(timeUtils.getDayBackground());
        protagonistLeft.setImage(protagonistImageLevel1);

        // Load characters from JSON
        Npc.loadNpcs();
        EnemyNpc.loadEnemies();

        // Set the first NPC
        timeUtils.updateGameTimeDisplay(timeCurrent, gameTime);

        // Show initial option buttons and hide the response area
        optionUtils.showOptionButtons(option1, option2, option3);
        dialogueUtils.hideResponseArea(npcResponseLabel, responseArea);

        // Clear any pending rewards
        rewardService.clearPendingRewards();

        // Initialize the ObjectiveService with protagonist and objective images
        objectiveService = new ObjectiveService(protagonist, objectiveImage1, objectiveImage2, objectiveImage3);
        objectiveService.updateObjectives();

        Logger.info("Game state initialized");

        // Start the game state with the first encounter
        advanceGameState();
    }


    private void startGameClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), event -> advanceTime()));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void advanceTime() {
        if (isGamePaused) {
            return;
        }

        LocalTime previousGameTime = gameTime;
        gameTime = gameTime.plusMinutes(60);

        if (previousGameTime.isAfter(gameTime)) {
            resetDailyEncounterCount();
            incrementDays();
        }

        updateTimeAndBackgroundDisplay();
        triggerEncounters();
        handleRewards();
    }

    private void resetDailyEncounterCount() {
        dailyEncounterCount = 0;
    }

    private void incrementDays() {
        days++;
        daysLabel.setText("Days: " + days);
    }

    private void updateTimeAndBackgroundDisplay() {
        timeUtils.updateGameTimeDisplay(timeCurrent, gameTime);
        timeUtils.updateBackgroundAndTimeOfDay(gameTime, gameBackground, timeDay);
    }

    private void triggerEncounters() {
        if ((gameTime.equals(LocalTime.of(7, 0)) || gameTime.equals(LocalTime.of(13, 0)) || gameTime.equals(LocalTime.of(19, 0)))
                && dailyEncounterCount < MAX_DAILY_ENCOUNTERS) {
            optionUtils.showOptionButtons(option1, option2, option3);
            gameUtils.fadeIn(npcsRight, 1);

            advanceGameState();
            dailyEncounterCount++;

            objectiveService.onNpcInteraction(objectiveLabel2Counter);
        }
    }

    private void handleRewards() {
        if (gameTime.equals(LocalTime.of(1, 0))) {
            optionUtils.hideOptionButtons(option1, option2, option3);
            gameUtils.fadeOut(npcsRight, 1);
            dailyScoreUpdate();
            showRewardResponse();
        }
    }

    private void calculateScore() {
        int hp = protagonist.getHealth();
        int gold = protagonist.getGold();
        int level = protagonist.getLevel();
        int npcInteractions = objectiveService.getNpcInteractions();
        boolean dragonBonus = objectiveService.isDragonDead();

        score = (hp * 10) + (gold * 5) + (level * 100) + (npcInteractions * 2) + (dragonBonus ? 500 : 0);
    }

    private void dailyScoreUpdate() {
        updateScoreLabel();
    }

    private void updateScoreLabel() {
        calculateScore();
        scoreLabel.setText("Score: " + score);
    }


    private void advanceGameState() {
        if (days < 10 || protagonist.getLevel() < 2) {
            npcRound();
        } else {
            if (lastRoundWasEnemy) {
                // Ha az előző enemyRound volt, visszaállítjuk az esélyt
                enemyChance = 0.2;
                lastRoundWasEnemy = false;
                npcRound();
            } else {
                if (new Random().nextDouble() < enemyChance) {
                    enemyRound();
                    lastRoundWasEnemy = true;
                } else {
                    npcRound();
                    // Növeljük az esélyt az enemyRound-ra, amíg el nem éri az 50%-ot
                    enemyChance = Math.min(0.5, enemyChance + 0.1);
                }
            }
        }
    }

    // Set's Npc-s up for the JavaFx
    public void setNpc(boolean isNpc) {
        dialogueUtils.hideResponseArea(npcResponseLabel, responseArea);
        if (isNpc) {
            currentNpc = npcService.selectNpc(Npc.getNpcs(), protagonist);

            if (currentNpc != null) {
                Logger.info("Selected NPC: " + currentNpc.getName() + " with " + currentNpc.getDialogues().size() + " dialogues");

                Image characterImage = npcService.loadNpcImage(currentNpc.getPath());

                if (characterImage != null) {
                    npcsRight.setImage(characterImage);  // Set image in ImageView
                }
            } else {
                Logger.error("No NPCs available.");
            }
        } else {
            currentEnemyNpc = npcService.selectEnemyNpc(EnemyNpc.getEnemies(), protagonist);

            // scale up the enemies for better difficulty

            var currentHP = currentEnemyNpc.getHealth();

            for (int i = 0; i < days; i += multiplierAfterXDays) {
                currentHP *= (int) Math.floor((1+enemyHpScale));
            }

            currentEnemyNpc.setHealth(currentHP);


            if (currentEnemyNpc != null) {
                Logger.info("Selected EnemyNpc: " + currentEnemyNpc.getName() + " with " + currentEnemyNpc.getDialogues().size() + " dialogues");

                Image characterImage = npcService.loadEnemyNpcImage(currentEnemyNpc.getPath());

                if (characterImage != null) {
                    npcsRight.setImage(characterImage);  // Set image in ImageView
                }
            } else {
                Logger.error("No enemies available.");
            }
        }
    }

    private void npcRound() {
        dialogueUtils.hideResponseArea(npcResponseLabel, responseArea);
        setNpc(true);

        if (currentNpc != null && currentNpc.getDialogues() != null && !currentNpc.getDialogues().isEmpty()) {
            // Pick a random dialogue from the current NPC's dialogues
            Dialogue randomDialogue = dialogueService.getRandomDialogue(currentNpc.getDialogues());

            setDialogueDetails(randomDialogue, currentNpc.getName(), currentNpc.getReliability() + "%");

            Logger.info("Random dialogue selected: " + randomDialogue.getText());
        } else {
            questText.setText("No dialogue available.");
            Logger.warn("No dialogues available for NPC: " + currentNpc);
        }

        if (protagonist.isAlive()) {
            Logger.info("Protagonist is alive");
            gameLogicService.levelUp(protagonist, protagonistLeft, levelLabel, objectiveService);
        }
    }

    private void enemyRound() {
        setNpc(false);

        if (currentEnemyNpc != null && currentEnemyNpc.getDialogues() != null && !currentEnemyNpc.getDialogues().isEmpty()) {
            // Pick a random dialogue from the current NPC's dialogues
            Dialogue randomDialogue = dialogueService.getRandomDialogue(currentEnemyNpc.getDialogues());

            setDialogueDetails(randomDialogue, currentEnemyNpc.getName(), currentEnemyNpc.getHealth() + "");

            Logger.info("Random dialogue selected: " + randomDialogue.getText());
        } else {
            questText.setText("No dialogue available.");
            Logger.warn("No dialogues available for NPC: " + currentNpc);
        }

        if (protagonist.isAlive()) {
            Logger.info("Protagonist is alive");
            gameLogicService.levelUp(protagonist, protagonistLeft, levelLabel, objectiveService);
        }
    }

    public void setDialogueDetails(Dialogue dialogue, String name, String reliability) {
        // Update the quest stats (rewards, quest text, etc.)
        updateQuestDetails(dialogue, name, reliability);

        // Set the options for the dialogue
        dialogueService.setOptionButtons(dialogue, option1, option2, option3);
    }

    // Method to update quest-related stats such as rewards, quest info, and reliability
    private void updateQuestDetails(Dialogue dialogue, String name, String reliability) {
        // Set the reward details
        rewardType1 = dialogue.getRewardType1();
        rewardType2 = dialogue.getRewardType2();
        // int days2 = days / 2; To decrease difficulty we can add a days2 to help grow the reward.
        // TODO test the game to know how balance
        number1 = gameLogicService.generateNumber(rewardType1, protagonist, currentNpc.getReliability(), days);
        number2 = gameLogicService.generateNumber(rewardType2, protagonist, currentNpc.getReliability(), 0);   //days2);
        questReward.setText(gameUtils.formattedReward(rewardType1, rewardType2, number1, number2));

        // Set the dialogue text
        questText.setText(gameUtils.replacePlaceholders(dialogue.getText(), number1, number2));
        questTextInfo.setText("What the " + name + " says:");
        // Set the quest type and reward details
        questType.setText(dialogue.getType().substring(0, 1).toUpperCase() + dialogue.getType().substring(1));
        questInfo.setText(gameUtils.replacePlaceholders(dialogue.getInfo(), number1, number2));
        // Set the reliability info
        questReliabilityInfo.setText("Should you trust the " + name + "?");
        questReliability.setText(name + "'s reliability is " + reliability);
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
            optionUtils.hideOptionButtons(option1, option2, option3);
        }
    }

    private void handleOptionSelection(DialogueOption option, String type1, int value1, String type2, int value2, boolean questAccepted, double npcReliability) {
        if (option != null) {
            dialogueUtils.showResponse(option.getResponse(), npcResponseLabel, responseArea);

            if (questAccepted) {
                // Check if type1 is not "none" and has enough resources.
                if (!Objects.equals(type1, "none") && gameLogicService.hasEnoughResources(protagonist, type1, value1)) {
                    // Deduct the required amount (value1) from type1.
                    rewardService.addPendingReward(type1, -value1, npcReliability);

                    // Add the second reward/penalty (type2) as usual.
                    rewardService.addPendingReward(type2, value2, npcReliability);
                } else if (Objects.equals(type1, "none")) {
                    // If type1 is "none", add the second reward/penalty (type2).
                    rewardService.addPendingReward(type2, value2, npcReliability);
                }
            }
        } else {
            npcResponseLabel.setText("No response available.");
            dialogueUtils.resetResponseArea(npcResponseLabel, responseArea);
        }
    }

    private void showRewardResponse() {
        // Delegate to RewardService
        String feedback = rewardService.applyPendingRewards(protagonist);

        // Update protagonist stats in the UI
        gameUtils.updateProtagonistStats(protagonist, hpLabel, goldLabel, damageLabel);

        // Show feedback to the player
        dialogueUtils.showResponse(feedback, npcResponseLabel, responseArea);
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