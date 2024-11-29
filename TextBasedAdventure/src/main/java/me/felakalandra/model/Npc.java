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
    private List<Dialogue> dialogues;

    @Getter
    @FXML
    private static List<Npc> npcs = new ArrayList<>();

    // Load NPCs using the Loader class
    public static void loadNpcs() {
        npcs = NpcLoader.loadData("Data/Characters.Json", Npc.class);
    }

    // Get image for NPC
    public static Image getImage(String path) {
        return NpcLoader.getImage(path);
    }

    public static boolean reliable(Npc npc) {
        Random random = new Random();
        int randomIndex = random.nextInt(100);
        return randomIndex <= npc.getReliability();
    }
}
