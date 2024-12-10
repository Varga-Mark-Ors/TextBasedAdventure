package me.felakalandra.util.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.felakalandra.controller.GameController;

import java.io.File;
import java.io.IOException;

public class SaveManager {
    private static final String SAVE_FILE = "game_save.json";
    private final ObjectMapper objectMapper;

    public SaveManager() {
        this.objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveGame(GameController controller) {
        try {
            GameState gameState = new GameState(controller.getProtagonist(), controller);
            objectMapper.writeValue(new File(SAVE_FILE), gameState);
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public GameState loadGame() {
        try {
            // Beolvassa a JSON fájlt GameState osztály példányává alakítva
            GameState gameState = objectMapper.readValue(new File(SAVE_FILE), GameState.class);
            System.out.println("Game loaded successfully!");
            return gameState;
        } catch (IOException e) {
            System.err.println("Failed to load game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


}
