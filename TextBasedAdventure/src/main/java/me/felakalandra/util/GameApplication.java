package me.felakalandra.util;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.felakalandra.model.Npcs;

import java.io.IOException;
import java.util.Objects;

public class GameApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Npcs.loadCharacters();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/GameView.fxml")));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Game");
        primaryStage.setResizable(true);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}