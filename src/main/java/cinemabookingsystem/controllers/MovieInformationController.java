package cinemabookingsystem.controllers;

import cinemabookingsystem.apis.ImdbApi;
import cinemabookingsystem.bookingsystem.CinemaBookingSystem;
import cinemabookingsystem.gui.Gui;
import cinemabookingsystem.gui.SceneCreator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Displays information about the movie to the user, such as the trailer,
 * date and time of screenings, the plot, and the rating.
 *
 *
 * @author Benjamin Nilsson
 * @since 31.08.2021
 */
public class MovieInformationController implements Initializable {

    @FXML
    private Text plot, genre;

    @FXML
    private ImageView trailerPoster, moviePoster;

    @FXML
    private Label movieName;

    @FXML
    private ComboBox<String> timeDropDownList, dateDropDownList;

    private ImdbApi imdb;
    private CinemaBookingSystem cinemaBookingSystem;

    public MovieInformationController() {
        this.cinemaBookingSystem = Gui.getBookingSystemObject();
        imdb = new ImdbApi();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // gets the movie information that the user clicked on
        var selectedMovie = cinemaBookingSystem.getMovie();
        var movieYear = cinemaBookingSystem.getYear();

        // displays the movie information for the user
        trailerPoster.setImage(new Image(cinemaBookingSystem.getTrailerImage()));
        moviePoster.setImage(new Image(imdb.getPoster(selectedMovie, movieYear)));
        movieName.setText(selectedMovie);
        genre.setText(imdb.getRating(selectedMovie, movieYear));
        plot.setText(imdb.getPlot(selectedMovie, movieYear));

        setDates();
        setTime();
        setDateAndTimeStartValues();
    }

    /**
     * The movie dates available for the user.
     */
    public void setDates() {
        dateDropDownList.getItems().addAll(
                "22/9",
                "23/9",
                "24/9",
                "25/9",
                "26/9",
                "27/9"
        );
    }

    /**
     * The screening time for the movies.
     */
    public void setTime() {
        timeDropDownList.getItems().addAll(
                "17:45",
                "21:00"
        );
    }

    /**
     * Selects the first date and time in each list as the default value.
     */
    public void setDateAndTimeStartValues() {
        dateDropDownList.setValue(dateDropDownList.getItems().get(0));
        timeDropDownList.setValue(timeDropDownList.getItems().get(0));
        cinemaBookingSystem.setDate(dateDropDownList.getValue());
        cinemaBookingSystem.setTime(timeDropDownList.getValue());
    }



    /**
     * Redirects the user to the Trailer Scene.
     *
     * @param mouseEvent Mouse event represents a click on the play button.
     */
    public void play(MouseEvent mouseEvent) throws Exception {
        cinemaBookingSystem.setPreviousScene("/scenes/MovieInformationScene.fxml");
        SceneCreator.launchScene("/scenes/TrailerScene.fxml");
    }

    /**
     * Redirects the user to the Home Scene.
     *
     * @param mouseEvent Mouse event representing a click on the logo button.
     * @throws Exception
     */
    public void openHome(MouseEvent mouseEvent) throws Exception {
        SceneCreator.launchScene("/scenes/HomeScene.fxml");
    }

    /**
     * Redirects the user to the Movies Scene.
     *
     * @param mouseEvent Mouse event representing a click on the movies and trailers button.
     * @throws Exception
     */
    public void openMovies(MouseEvent mouseEvent) throws Exception {
        SceneCreator.launchScene("/scenes/MoviesScene.fxml");
    }

    /**
     * Redirects the user to the Login Scene.
     *
     * @param mouseEvent Mouse event representing a click on the logout button.
     * @throws Exception
     */
    public void logout(MouseEvent mouseEvent) throws Exception {
        SceneCreator.launchScene("/scenes/LoginScene.fxml");
    }

    /**
     * Redirects the user to the Account Scene.
     *
     * @param mouseEvent Mouse event representing a click on the My Account button.
     * @throws Exception
     */
    public void openAccount(MouseEvent mouseEvent) throws Exception {
        SceneCreator.launchScene("/scenes/AccountScene.fxml");
    }

    /**
     * Sets up values for the Booking Scene, such as the date and time of screening.
     * Redirects the user to the Booking Scene.
     *
     * @param mouseEvent Mouse event represents a click on the reserve button.
     * @throws Exception
     */
    public void openBooking(MouseEvent mouseEvent) throws Exception {
        cinemaBookingSystem.setDate(dateDropDownList.getValue());
        cinemaBookingSystem.setTime(timeDropDownList.getValue());
        SceneCreator.launchScene("/scenes/BookingScene.fxml");
    }

    /**
     * Redirects the user to My Reservations Scene.
     *
     * @param mouseEvent Mouse event representing a click on my reservations button.
     * @throws Exception
     */
    public void openMyReservations(MouseEvent mouseEvent) throws Exception{
        SceneCreator.launchScene("/scenes/ReservationsScene.fxml");
    }
}
