package me.felakalandra.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import lombok.Data;
import lombok.Getter;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
public class EnemyNpc {

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
    @JsonProperty("health")
    private int health;

    @FXML
    @JsonProperty("dialogues")
    private List<Dialogue> dialogues; // New field to store the dialogues

    @Getter
    @FXML
    private static List<EnemyNpc> enemies = new ArrayList<>();

    // Static method to load NPCs from JSON
    public static void loadEnemies() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Load the file as a resource from the classpath
            InputStream inputStream = EnemyNpc.class.getClassLoader().getResourceAsStream("Data/EnemyCharacters.json");
            if (inputStream != null) {
                Logger.info("JSON file found and loading.");
                // Deserialize JSON into a list of Npc objects
                List<EnemyNpc> loadedEnemies = objectMapper.readValue(
                        inputStream,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, EnemyNpc.class)
                );

//                // Log each NPC as it's loaded
//                for (Npc npc : loadedNpcs) {
//                    Logger.info("Loaded NPC: " + npc);
//                }

                // Update the static list with the loaded NPCs
                enemies.clear();
                enemies.addAll(loadedEnemies);
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
            InputStream imageStream = EnemyNpc.class.getClassLoader().getResourceAsStream(path);
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
                ", health=" + health +
                ", dialogues=" + dialogues +
                '}';
    }

    public String fight(Protagonist player) {
        if (player.getDamagePoints() >= this.getHealth()) {
            player.decreaseHealth((int) (player.getHealth() * 0.1));
            return "win";
        }
        var dif = this.getHealth() - player.getDamagePoints();

        if (dif <30){
            boolean win = new Random().nextDouble() < 0.75;
            if (win) {
                player.decreaseHealth((int) (player.getHealth() * 0.15));
                return "win";
            } else {
                player.decreaseHealth((int) (player.getHealth() * 0.50));
                return "lose";
            }
        } else {
            boolean win = new Random().nextDouble() < 0.30;
            if (win) {
                player.decreaseHealth((int) (player.getHealth() * 0.35));
                return "win";
            } else {
                player.decreaseHealth((int) (player.getHealth() * 0.50));
                return "lose";
            }
        }
    }

    public void run(Protagonist player){
        player.runningGoldLose();
        var rnd = new Random().nextInt(10);
        if (rnd < 5){
            player.decreaseHealth((int) (player.getHealth() * 0.1));
        }
    }

}
