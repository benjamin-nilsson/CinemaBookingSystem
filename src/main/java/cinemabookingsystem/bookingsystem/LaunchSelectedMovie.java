package cinemabookingsystem.bookingsystem;

import cinemabookingsystem.gui.Gui;
import cinemabookingsystem.gui.SceneCreator;

/**
 * Sets up the information for the movie which the user clicked on, to then
 * redirect the user to the Movie Information Scene which displays the information
 * specific to that movie
 *
 * @author Benjamin Nilsson
 * @since 31.08.2021
 */
public class LaunchSelectedMovie {
    private CinemaBookingSystem cinemaBookingSystem;

    public LaunchSelectedMovie() {
        this.cinemaBookingSystem = Gui.getBookingSystemObject();
    }

    /**
     * Sets up the information for the movie which the user clicked on, to then
     * redirect the user to the Movie Information Scene which displays the information
     * specific to that movie
     *
     * @param movie the movie the user is interested in
     * @param year the year the movie came out
     * @param movieTrailerImage the poster made for the movie
     * @param movieTrailer the trailer for the movie
     */
    public void selectedMovie(String movie, int year, String movieTrailerImage, String movieTrailer) {
        try {
            cinemaBookingSystem.setTrailerImage(movieTrailerImage);
            cinemaBookingSystem.setTrailer(movieTrailer);
            cinemaBookingSystem.setMovie(movie);
            cinemaBookingSystem.setYear(year);
            SceneCreator.launchScene("/scenes/MovieInformationScene.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
