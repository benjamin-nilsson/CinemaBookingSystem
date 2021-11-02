package cinemabookingsystem.controllers;

import cinemabookingsystem.apis.ImdbApi;
import cinemabookingsystem.bookingsystem.LaunchSelectedMovie;
import cinemabookingsystem.database.CinemaDatabase;
import cinemabookingsystem.gui.SceneCreator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Displays the specified movies to the user and the user can click on each movies poster
 * for further information
 *
 * @author Benjamin Nilsson
 * @since 31.08.2021
 */
public class MoviesController implements Initializable {

    @FXML
    private ImageView firstPoster, secondPoster, thirdPoster, fourthPoster, fifthPoster,
                      sixthPoster, seventhPoster, eightPoster, ninthPoster, tenthPoster;

    @FXML
    private Text movie1, movie2, movie3, movie4, movie5, movie6, movie7, movie8, movie9, movie10;

    private ImdbApi imdbApi;
    private LaunchSelectedMovie launchSelectedMovie;
    private CinemaDatabase cinemaDatabase;

    public MoviesController() {
        imdbApi = new ImdbApi();
        launchSelectedMovie = new LaunchSelectedMovie();
        cinemaDatabase = new CinemaDatabase();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Text> movieTitles = List.of(
                movie1, movie2, movie3, movie4, movie5,
                movie6, movie7, movie8, movie9, movie10
        );

        List<ImageView> moviePosters = List.of(
                firstPoster, secondPoster, thirdPoster, fourthPoster, fifthPoster,
                sixthPoster, seventhPoster, eightPoster, ninthPoster, tenthPoster
        );

        int index = 0;
        // Goes through all titles of a movie and retrieves that movies information and
        // with this information sets up and if clicked on launches the selected movie scene.
        List<String> moviesInTheater = cinemaDatabase.getMoviesInTheater();
        for (String movie : moviesInTheater) {
            movie = movie.replaceAll("'", "''");
            List<String> movieInformation = cinemaDatabase.getMovieInTheater(movie);
            setMovie(
                movieTitles.get(index), moviePosters.get(index),
                movieInformation.get(0), Integer.parseInt(movieInformation.get(1)),
                movieInformation.get(2), movieInformation.get(3)
            );
            index++;
        }
    }

    /**
     * Displays the title, poster, and trailer image of a movie.
     * Launches the next scene and sends the given movie information with it.
     * @param movie the movie we want to be selected.
     * @param poster the poster for the selected movie
     * @param movieTitle the title of the movie.
     * @param movieYear the year that the movie came out.
     * @param movieTrailerImagePath the path where the trailer image is saved.
     * @param movieTrailerPath the path where the trailer is saved.
     */
    private void setMovie(Text movie, ImageView poster, String movieTitle, int movieYear,
                          String movieTrailerImagePath, String movieTrailerPath) {

        movie.setText(movieTitle);
        poster.setImage(new Image(imdbApi.getPoster(movieTitle, movieYear)));
        poster.setOnMouseClicked(e -> {
            launchSelectedMovie.selectedMovie(movieTitle, movieYear,
                    movieTrailerImagePath,
                    movieTrailerPath);
        });
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
     * Redirects the user to the Home Scene.
     *
     * @param mouseEvent Mouse event representing a click on the logo button.
     * @throws Exception
     */
    public void openHome(MouseEvent mouseEvent) throws Exception {
        SceneCreator.launchScene("/scenes/HomeScene.fxml");
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
     * Redirects the user to My Reservations Scene.
     *
     * @param mouseEvent Mouse event representing a click on my reservations button.
     * @throws Exception
     */
    public void openMyReservations(MouseEvent mouseEvent) throws Exception{
        SceneCreator.launchScene("/scenes/ReservationsScene.fxml");
    }
}
