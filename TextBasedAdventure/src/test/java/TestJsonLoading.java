import com.fasterxml.jackson.databind.ObjectMapper;
import me.felakalandra.model.Npc;

import java.io.InputStream;
import java.util.List;

public class TestJsonLoading {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InputStream inputStream = TestJsonLoading.class.getClassLoader().getResourceAsStream("data/Characters.Json");
            if (inputStream != null) {
                List<Npc> characters = objectMapper.readValue(
                        inputStream,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Npc.class)
                );
                System.out.println("Characters loaded successfully: " + characters);
            } else {
                System.err.println("JSON file not found in resources.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
