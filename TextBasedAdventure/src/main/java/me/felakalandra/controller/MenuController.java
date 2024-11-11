package me.felakalandra.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.io.IOException;

public class MenuController {

    @FXML
    public Button continueGame;

    // Continue the current game
    @FXML
    public void continueCurrentGame(ActionEvent actionEvent) {
        Stage stage = (Stage) continueGame.getScene().getWindow();
        stage.close();
    }

    // Start a new game

    @FXML
    public void startNewGame(ActionEvent actionEvent) {
        // Close the current menu
        Stage currentStage = (Stage) continueGame.getScene().getWindow();
        currentStage.close();

        // Creates the main game window again
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/GameView.fxml"));
            AnchorPane root = fxmlLoader.load();


            GameController gameController = fxmlLoader.getController();
            gameController.resetGame();

            Scene scene = new Scene(root);
            Stage primaryStage = new Stage();
            primaryStage.setTitle("New Game");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("New game started");
    }

    // Toggle sound on or off
    @FXML
    private void handleToggleSound() {
        Logger.info("Sound toggled");
    }

    // Show the options menu
    @FXML
    public void showOptions(ActionEvent actionEvent) {
        Logger.info("Options menu opened");
    }

    // Exit the game
    @FXML
    private void exitGame() {
        Platform.exit();
    }
}