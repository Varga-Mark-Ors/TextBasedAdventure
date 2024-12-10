package me.felakalandra.util.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.felakalandra.controller.GameController;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;

public class SaveManager {
    // The file save and load path.
    private static final String SAVE_FILE = "resources/SavedGames/game_save.json";
    private final ObjectMapper objectMapper;

    public SaveManager() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveGame(GameController controller) {
        try {
            // Check if the folder exists, if not, create it.
            File saveDir = new File("resources/SavedGames");
            if (!saveDir.exists()) {
                boolean dirCreated = saveDir.mkdirs();  // Create the folder if it does not exist
                if (!dirCreated) {
                    Logger.error("Nem sikerült létrehozni a mappát!");
                    return;
                }
            }

            // Save the GameState object in JSON format.
            GameState gameState = new GameState(controller.getProtagonist(), controller);
            objectMapper.writeValue(new File(SAVE_FILE), gameState);
            Logger.info("Game saved successfully!");
        } catch (IOException e) {
            Logger.error("Failed to save game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public GameState loadGame() {
        try {
            // Loading the JSON file and converting it into a GameState object.
            File saveFile = new File(SAVE_FILE);
            if (!saveFile.exists()) {
                Logger.error("A fájl nem található: " + SAVE_FILE);
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