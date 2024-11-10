package me.felakalandra.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

public class StartUpController {
    @FXML
    private AnchorPane startUpBase;

    @FXML
    private void initialize() {
        startUpBase.setBackground(Background.fill(Color.LIGHTBLUE));

        System.out.println("StartUpController initialized");
    }
}
