package cinemabookingsystem.controllers;

import cinemabookingsystem.bookingsystem.CinemaBookingSystem;
import cinemabookingsystem.gui.Gui;
import cinemabookingsystem.gui.SceneCreator;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller for Home Scene.
 * Shows an example of movies that the user can watch.
 *
 * @author Benjamin Nilsson
 * @since 31.08.2021
 */
public class TrailerController implements Initializable {
    @FXML
    private ImageView exitTrailerButton, playAndPauseButton;

    @FXML
    private Label playTime;

    @FXML
    private MediaView mediaView;

    @FXML
    private Slider timeSlider, volumeSlider;

    @FXML
    private GridPane toolBar;

    private File file;
    private Media media;
    private MediaPlayer player;
    private Duration duration;
    private CinemaBookingSystem cinemaBookingSystem;

    public TrailerController() {
        cinemaBookingSystem = Gui.getBookingSystemObject();

        // set up the media player with the trailer of the movie that is being shown to the user
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Handles listening events which allows us to fast forward or rewind
        // the trailer in accordance with the time duration.

        file = new File(cinemaBookingSystem.getTrailer());
        media = new Media(file.toURI().toString());
        player = new MediaPlayer(media);
        mediaView.setMediaPlayer(player);

        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                duration = player.getMedia().getDuration();
                updateValues();
            }
        });

        play();
    }

    /**
     * Makes the trailer window visible for the user and starts the trailer.
     * This method also enables the user to fast forward or rewind, play and pause,
     * as well as adjust the volume.
     */
    public void play() {
        player.play();
        playAndPauseButton.setVisible(true);

        // Listener for the time slider allowing it to move forward according to
        // the time duration which is also handled by this listener.
        player.currentTimeProperty().addListener(new InvalidationListener()
        {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });

        // listens to the users adjustments of the time slider
        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable observable) {
                if (timeSlider.isValueChanging())
                    player.seek(duration.multiply(timeSlider.getValue() / 100));
            }
        });

        // listens to the users adjustments of the volume slider
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging())
                    player.setVolume(volumeSlider.getValue() / 100.0);
            }
        });
    }

    /**
     * Hides the and stops the trailer upon exit.
     *
     * @param mouseEvent Mouse event represents a click on the close button.
     */
    public void closeTrailer(MouseEvent mouseEvent) throws Exception {
        player.stop();
        SceneCreator.launchScene(cinemaBookingSystem.getPreviousScene());
    }

    /**
     * Plays or stops the trailer depending on its status.
     *
     * @param mouseEvent Mouse event represent a click on the play and pause button.
     */
    public void pauseAndPlay(MouseEvent mouseEvent) {
        var status = player.getStatus();
        if (status == MediaPlayer.Status.PLAYING) {
            player.pause();
            playAndPauseButton.setImage(new Image("imagesandtrailers/play.png"));
        }
        else {
            player.play();
            playAndPauseButton.setImage(new Image("imagesandtrailers/pause.png"));
        }
    }

    /**
     * Shows the time slider and the sound adjuster along with the play and paus button.
     *
     * @param mouseEvent Mouse event represents a mouse entering the trailer window
     */
    public void showToolBar(MouseEvent mouseEvent) {
        exitTrailerButton.setVisible(true);
        toolBar.setVisible(true);
        timeSlider.setVisible(true);
    }

    /**
     * Hides the time slider and the sound adjuster along with the play and pause button.
     *
     * @param mouseEvent Mouse event represents a mouse leaving the trailer window
     */
    public void hideToolBar(MouseEvent mouseEvent) {
        exitTrailerButton.setVisible(true);
        toolBar.setVisible(false);
        timeSlider.setVisible(false);
    }

    /**
     * update the toolbar with the time duration of the trailer as it's being shown
     */
    private void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = player.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration).toMillis()
                                * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int)Math.round(player.getVolume()
                                * 100));
                    }
                }
            });
        }
    }

    /**
     * Calculates the elapsed time the media player has been playing and formats it
     * to be displayed on the control toolbar.
     *
     * @param elapsed calculating how far the into the trailer the viewer is
     * @param duration calculating how long the trailer is
     * @return both the elapsed time and the time length of the complete trailer
     */
    private String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int)Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0)
            intElapsed -= elapsedHours * 60 * 60;
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int)Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0)
                intDuration -= durationHours * 60 * 60;
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 -
                    durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            }
            else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds,durationMinutes,
                        durationSeconds);
            }
        }
        else {
            if (elapsedHours > 0)
                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            else
                return String.format("%02d:%02d",elapsedMinutes, elapsedSeconds);
        }
    }
}