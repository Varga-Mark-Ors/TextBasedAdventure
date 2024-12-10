package me.felakalandra.util.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.felakalandra.controller.GameController;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;

public class SaveManager {
    private static final String SAVE_DIRECTORY = "TextBasedAdventure/src/main/resources/SavedGames/";
    private final ObjectMapper objectMapper;

    public SaveManager() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveGame(String saveName, GameController controller) {
        try {
            // Check if the folder exists, if not, create it.
            File saveDir = new File("TextBasedAdventure/src/main/resources/SavedGames");
            if (!saveDir.exists()) {
                boolean dirCreated = saveDir.mkdirs();  // Create the folder if it does not exist
                if (!dirCreated) {
                    Logger.error("Couldn't cteare folder");
                    return;
                }
            }

            // Save the GameState object in JSON format.
            GameState gameState = new GameState(controller.getProtagonist(), controller);

            // Save the game state to a JSON file with the custom save name
            File saveFile = new File(SAVE_DIRECTORY + saveName + ".json");
            objectMapper.writeValue(saveFile, gameState);
            Logger.info("Game saved successfully with name: " + saveName);
        } catch (IOException e) {
            Logger.error("Failed to save game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public GameState loadGame() {
        try {
            // Loading the JSON file and converting it into a GameState object.
            File saveFile = new File(SAVE_DIRECTORY);
            if (!saveFile.exists()) {
                Logger.error("A fájl nem található: " + SAVE_DIRECTORY);
                return null;
            }

            GameState gameState = objectMapper.readValue(saveFile, GameState.class);
            Logger.info("Game loaded successfully!");
            return gameState;
        } catch (IOException e) {
            Logger.error("Failed to load game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}