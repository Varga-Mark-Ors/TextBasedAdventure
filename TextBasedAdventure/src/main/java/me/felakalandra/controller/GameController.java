package me.felakalandra.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.io.IOException;

public class GameController {
    @FXML
    private AnchorPane gameBase;

    @FXML
    private Button menuButton;

    @FXML
    private void initialize() {
        gameBase.setBackground(Background.fill(Color.LIGHTBLUE));

        Logger.info("GameController initialized");
    }

    @FXML
    private void showMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Menu.fxml"));
            VBox settingsPane = fxmlLoader.load();

            // Create a new Stage for the Settings window
            Stage settingsStage = new Stage();
            settingsStage.setTitle("Settings");

            settingsStage.initModality(Modality.APPLICATION_MODAL); // Makes it a modal (popup) window
            settingsStage.setResizable(false);

            Scene scene = new Scene(settingsPane);
            settingsStage.setScene(scene);

            // Show the settings window
            settingsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
