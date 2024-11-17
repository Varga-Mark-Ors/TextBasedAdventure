package me.felakalandra.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Dialogue {

    @JsonProperty("text")
    private String text;

    @JsonProperty("type")
    private String type; // Could be "quest", "trade", "hint" or others

    @JsonProperty("info")
    private String info;

    @JsonProperty("reward")
    private Map<String, Object> reward;  // Store reward as a map, since it can be different types (e.g., gold, hp, items)

    @JsonProperty("options")
    private Map<String, DialogueOption> options;  // Store options as a map (option1, option2, etc.)

    @JsonCreator
    public Dialogue(@JsonProperty("text") String text, @JsonProperty("type") String type,
                    @JsonProperty("info") String info, @JsonProperty("reward") Map<String, Object> reward,
                    @JsonProperty("options") Map<String, DialogueOption> options) {
        this.text = text;
        this.type = type;
        this.info = info;
        this.reward = reward;
        this.options = options;
    }

    @Override
    public String toString() {
        return "Dialogue{" +
                "text='" + text + '\'' +
                ", type='" + type + '\'' +
                ", info='" + info + '\'' +
                ", reward=" + reward +
                ", options=" + options +
                '}';
    }
}
