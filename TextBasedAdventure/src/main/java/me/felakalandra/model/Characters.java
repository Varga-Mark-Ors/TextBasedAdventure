package me.felakalandra.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Characters {

    @FXML
    private String name;

    @FXML
    private static List<Character> characters = new ArrayList<>();

    @FXML
    private String filePath = "src/main/java/me/felakalandra/data/Characters.Json";

    public Characters() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON fájl beolvasása és a 'characters' lista feltöltése
            characters = objectMapper.readValue(new File(filePath), objectMapper.getTypeFactory().constructCollectionType(List.class, Characters.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
