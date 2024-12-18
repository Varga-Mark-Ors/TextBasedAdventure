package me.felakalandra.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DialogueOption {
    private Map<String, Integer> statChanges;

    @JsonProperty("text")
    private String text;

    @JsonProperty("response")
    private String response;

    @JsonCreator
    public DialogueOption(@JsonProperty("text") String text, @JsonProperty("response") String response) {
        this.text = text;
        this.response = response;
    }

    @Override
    public String toString() {
        return "DialogueOption{" +
                "text='" + text + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
