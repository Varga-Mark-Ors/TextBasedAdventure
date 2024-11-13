package me.felakalandra.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Npc {

    @FXML
    @JsonProperty("name")  // JSON key mapping
    private String name;

    @FXML
    @JsonProperty("picture") // JSON key mapping
    private String path;

    // A static list to hold all `Characters` objects that have been read
    @Getter
    @FXML
    private static List<Npc> npcs = new ArrayList<>();

    // Static method to load characters from JSON
    public static void loadNpcs() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Load the file as a resource from the classpath
            InputStream inputStream = Npc.class.getClassLoader().getResourceAsStream("Data/Characters.Json");
            if (inputStream != null) {
                Logger.info("JSON file found and loading.");
                npcs = objectMapper.readValue(
                        inputStream,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Npc.class)
                );
                Logger.info("Characters loaded successfully.");
            } else {
                Logger.error("JSON file not found in resources. Please check the path.");
            }
        } catch (IOException e) {
            Logger.error("Error reading JSON file: " + e.getMessage());
        }
    }

    // Updated getImage method to return a JavaFX Image from a classpath resource
    public static Image getImage(String path) {
        try {
            // Load image as a resource from the classpath
            InputStream imageStream = Npc.class.getClassLoader().getResourceAsStream(path);
            if (imageStream != null) {
                //Logger.info("Image file found at the given path: " + path);
                return new Image(imageStream); // Create a JavaFX Image object from InputStream
            } else {
                Logger.info("Image file not found at the given path: " + path);
                return null;
            }
        } catch (Exception e) {
            Logger.error("Error loading image: " + e.getMessage());
            return null;
        }
    }
}