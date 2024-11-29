package me.felakalandra.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import lombok.Data;
import lombok.Getter;
import me.felakalandra.util.NpcLoader;

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
    private List<Dialogue> dialogues;

    @Getter
    @FXML
    private static List<EnemyNpc> enemies = new ArrayList<>();

    // Load enemies using the Loader class
    public static void loadEnemies() {
        enemies = NpcLoader.loadData("Data/EnemyCharacters.json", EnemyNpc.class);
    }

    // Get image for Enemy NPC
    public static Image getImage(String path) {
        return NpcLoader.getImage(path);
    }

    public String fight(Protagonist player) {
        if (player.getDamagePoints() >= this.getHealth()) {
            player.decreaseHealth((int) (player.getHealth() * 0.1));
            return "win";
        }
        var dif = this.getHealth() - player.getDamagePoints();

        if (dif < 30) {
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

    public void run(Protagonist player) {
        player.runningGoldLose();
        var rnd = new Random().nextInt(10);
        if (rnd < 5) {
            player.decreaseHealth((int) (player.getHealth() * 0.1));
        }
    }
}
