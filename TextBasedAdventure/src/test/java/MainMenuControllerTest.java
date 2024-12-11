import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import me.felakalandra.controller.MainMenuController;
import me.felakalandra.util.save.GameState;
import me.felakalandra.util.save.SaveManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainMenuControllerTest {

    private MainMenuController mainMenuController;
    private MediaPlayer mockMediaPlayer;
    private VBox mockSavedGamesBox;
    private VBox mockOptionsMenuBox;
    private Button mockToggleSoundButton;

    @BeforeEach
    void setUp() {
        // Initialize the MainMenuController and mock dependencies
        mainMenuController = new MainMenuController();
        mockMediaPlayer = mock(MediaPlayer.class);
        mockSavedGamesBox = mock(VBox.class);
        mockOptionsMenuBox = mock(VBox.class);
        mockToggleSoundButton = mock(Button.class);

        // Use reflection to inject mocked fields
        mainMenuController.savedGamesBox = mockSavedGamesBox;
        mainMenuController.optionsMenuBox = mockOptionsMenuBox;
        mainMenuController.handleToggleSoundButton = mockToggleSoundButton;

        // Replace the actual media player with a mock
        var mediaPlayerField = MainMenuController.class.getDeclaredField("mediaPlayer");
        mediaPlayerField.setAccessible(true);
        mediaPlayerField.set(mainMenuController, mockMediaPlayer);
    }

    @Test
    void testPlayMenuMusic_startsMusic() {
        // Arrange
        when(mockMediaPlayer.getStatus()).thenReturn(MediaPlayer.Status.READY);

        // Act
        mainMenuController.playMenuMusic();

        // Assert
        verify(mockMediaPlayer).setCycleCount(MediaPlayer.INDEFINITE);
        verify(mockMediaPlayer).play();
    }

    @Test
    void testHandleToggleSound_togglesMuteState() {
        // Arrange
        when(mockToggleSoundButton.getText()).thenReturn("Toggle Sound: ON");

        // Act: Mute the sound
        mainMenuController.handleToggleSound();

        // Assert: MediaPlayer volume is set to 0.0
        verify(mockMediaPlayer).setVolume(0.0);
        verify(mockToggleSoundButton).setText("Toggle Sound: OFF");

        // Act: Unmute the sound
        mainMenuController.handleToggleSound();

        // Assert: MediaPlayer volume is restored
        verify(mockMediaPlayer).setVolume(0.5);
        verify(mockToggleSoundButton).setText("Toggle Sound: ON");
    }

    @Test
    void testLoadSavedGame_withNoSavedGames_displaysAlert() {
        // Arrange
        File mockSaveDir = mock(File.class);
        when(mockSaveDir.listFiles(any())).thenReturn(new File[0]);

        // Act
        mainMenuController.loadSavedGame(null);

        // Assert
        verify(mockSavedGamesBox).setVisible(true);
        verify(mockSavedGamesBox).getChildren();
        verify(mockSavedGamesBox, never()).add(any());
    }

    @Test
    void testLoadSavedGame_withSavedGames_createsButtons() {
        // Arrange
        File mockSaveFile1 = mock(File.class);
        File mockSaveFile2 = mock(File.class);
        when(mockSaveFile1.getName()).thenReturn("save1.json");
        when(mockSaveFile2.getName()).thenReturn("save2.json");

        File[] savedFiles = {mockSaveFile1, mockSaveFile2};
        File mockSaveDir = mock(File.class);
        when(mockSaveDir.listFiles(any())).thenReturn(savedFiles);

        // Act
        mainMenuController.loadSavedGame(null);

        // Assert
        verify(mockSavedGamesBox, times(2)).add(any(Button.class));
    }

    @Test
    void testExitGame_stopsMusicAndExitsPlatform() {
        try (MockedStatic<Platform> mockedPlatform = Mockito.mockStatic(Platform.class)) {
            // Act
            mainMenuController.exitGame();

            // Assert
            mockedPlatform.verify(Platform::exit);
            verify(mockMediaPlayer).stop();
        }
    }
}
