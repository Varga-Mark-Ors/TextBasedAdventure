package me.felakalandra.util;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    // Background images based on the time of day
    @Getter
    private final Image dayBackground = new Image("Images/Background/daytime.jpg");
    private final Image nightBackground = new Image("Images/Background/night.jpg");
    private final Image dawnBackground = new Image("Images/Background/dawn.jpg");

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Change background and time of day at 8 PM and 7 AM
    public void updateBackgroundAndTimeOfDay(LocalTime gameTime, ImageView gameBackground, Label timeDay) {
        // Night: from 20:00 to 4:00
        if (gameTime.isAfter(LocalTime.of(20, 0)) || gameTime.isBefore(LocalTime.of(4, 0))) {
            gameBackground.setImage(nightBackground);
            timeDay.setText("Nighttime");
            // Dawn: from 3:50 to 8:00
        } else if (gameTime.isAfter(LocalTime.of(3, 50)) && gameTime.isBefore(LocalTime.of(8, 0))) {
            gameBackground.setImage(dawnBackground);
            timeDay.setText("Dawn");
            // Daytime: from 8:00 to 20:00
        } else {
            gameBackground.setImage(dayBackground);
            timeDay.setText("Daytime");
        }
    }

    public void updateGameTimeDisplay(Label timeCurrent, LocalTime gameTime) {
        timeCurrent.setText("Time: " + gameTime.format(TIME_FORMATTER));
    }
}
