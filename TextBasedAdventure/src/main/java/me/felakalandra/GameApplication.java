package me.felakalandra;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;

import java.io.IOException;
import java.util.Objects;

public class GameApplication extends Application {

    @Getter
    private Stage primaryStage;

    //To get GameApplication instance in MainMenu controller.
    @Getter
    private static GameApplication instance;

    public GameApplication() {
        instance = this;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        // Load MainMenu on application startup
        showMainMenu();
    }
    public void showMainMenu() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/MainMenu.fxml")));
        Scene scene = new Scene(root);
        //
        if (primaryStage.getScene() == null) {
            // "Set the style only if setScene has not been called yet."
            primaryStage.setTitle("Main Menu");
            primaryStage.setResizable(false);
            primaryStage.initStyle(StageStyle.UNDECORATED);
        }

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }


    // First game launch
    public void startGame() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/GameView.fxml")));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Game");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}