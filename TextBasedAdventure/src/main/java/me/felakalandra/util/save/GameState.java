package me.felakalandra.util.save;

import javafx.fxml.FXML;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.felakalandra.controller.GameController;
import me.felakalandra.model.Protagonist;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class GameState {

    private Protagonist protagonist;
    private LocalTime gameTime;
    private int days;
    private int score;

    public GameState(Protagonist protagonist, GameController controller) {
        this.protagonist = protagonist;
        this.gameTime = controller.getGameTime();
        this.days = controller.getDays();
        this.score = controller.getScore();
    }
}
