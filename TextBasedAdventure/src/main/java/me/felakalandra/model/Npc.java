package me.felakalandra.model;

import com.fasterxml.jackson.annotation.JsonCreator;
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
import java.util.Random;

@Getter
@Setter
public class Npc {

    @FXML
    @JsonProperty("name")
    private String name;

    @FXML
    @JsonProperty("picture")
    private String path;

    @FXML
    @JsonProperty("level")
    private int level;

    @FXML
    @JsonProperty("type")
    private NpcType type;

    @FXML
    @JsonProperty("reliability")
    private int reliability;

    @FXML
    @JsonProperty("dialogues")
    private List<Dialogue> dialogues; // New field to store the dialogues

    @Getter
    @FXML
    private static List<Npc> npcs = new ArrayList<>();

    // Static method to load NPCs from JSON
    public static void loadNpcs() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Load the file as a resource from the classpath
            InputStream inputStream = Npc.class.getClassLoader().getResourceAsStream("Data/Characters.Json");
            if (inputStream != null) {
                Logger.info("JSON file found and loading.");
                // Deserialize JSON into a list of Npc objects
                List<Npc> loadedNpcs = objectMapper.readValue(
                        inputStream,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Npc.class)
                );

//                // Log each NPC as it's loaded
//                for (Npc npc : loadedNpcs) {
//                    Logger.info("Loaded NPC: " + npc);
//                }

                // Update the static list with the loaded NPCs
                npcs.clear();
                npcs.addAll(loadedNpcs);
                Logger.info("NPCs loaded successfully.");
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
            InputStream imageStream = Npc.class.getClassLoader().getResourceAsStream(path);
            if (imageStream != null) {
                return new Image(imageStream);
            } else {
                Logger.info("Image file not found at the given path: " + path);
                return null;
            }
        } catch (Exception e) {
            Logger.error("Error loading image: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return "Npc{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", level=" + level +
                ", type=" + type +
                ", reliability=" + reliability +
                ", dialogues=" + dialogues +
                '}';
    }

    public static boolean reliable(Npc npc) {
        Random random = new Random();
        int randomIndex = random.nextInt(100);
        return randomIndex <= npc.getReliability();
    }
}
