package me.felakalandra.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Characters {

    @FXML
    @JsonProperty("name")  // JSON key mapping
    private String name;

    @FXML
    @JsonProperty("picture") // JSON key mapping
    private String path;

    // Egyéb hasznos metódusok, pl. a karakterek lekérése a listából
    // A list of static characters that contains all `Characters` objects that have been read
    @Getter
    @FXML
    private static List<Characters> characters = new ArrayList<>();

    //JSON file path
    @FXML
    private String filePath = "src/main/java/me/felakalandra/data/Characters.Json";

    // Constructor: Reading JSON and uploading a list of characters
    public Characters() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Reading a JSON file and uploading a static list of 'characters'
            characters = objectMapper.readValue(
                    new File(filePath),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Characters.class)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load an image from a file path and return it
    public static Image getImage(String path) {
        try {
            // Load image from the given path
            File imageFile = new File(path);
            if (imageFile.exists()) {
                return ImageIO.read(imageFile);  // Returns Image object
            } else {
                System.out.println("Image file not found at the given path: " + path);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
