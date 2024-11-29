package me.felakalandra.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NpcLoader {
    // Generic method to load data from JSON
    public static <T> List<T> loadData(String fileName, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<T> loadedData = new ArrayList<>();
        try {
            // Load the file as a resource from the classpath
            InputStream inputStream = NpcLoader.class.getClassLoader().getResourceAsStream(fileName);
            if (inputStream != null) {
                Logger.info("JSON file found and loading.");
                // Deserialize JSON into a list of objects
                loadedData = objectMapper.readValue(
                        inputStream,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
                );
                Logger.info(clazz.getSimpleName() + " data loaded successfully.");
            } else {
                Logger.error("JSON file not found in resources. Please check the path.");
            }
        } catch (IOException e) {
            Logger.error("Error reading JSON file: " + e.getMessage());
        }
        return loadedData;
    }

    // Generic method to get an image from the classpath
    public static Image getImage(String path) {
        try {
            InputStream imageStream = NpcLoader.class.getClassLoader().getResourceAsStream(path);
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
}
