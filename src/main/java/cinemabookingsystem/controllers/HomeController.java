package cinemabookingsystem.controllers;

import cinemabookingsystem.apis.ImdbApi;
import cinemabookingsystem.bookingsystem.CinemaBookingSystem;
import cinemabookingsystem.bookingsystem.LaunchSelectedMovie;
import cinemabookingsystem.database.CinemaDatabase;
import cinemabookingsystem.gui.Gui;
import cinemabookingsystem.gui.SceneCreator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * The controller for Home Scene.
 * Shows an example of movies that the user can watch.
 *
 * @author Benjamin Nilsson
 * @since 31.08.2021
 */
public class HomeController implements Initializable {

    @FXML
    private ImageView trailerPoster, firstPoster,
            secondPoster, thirdPoster, fourthPoster, fifthPoster;

    @FXML
    private Text currentMovie;

    private ImdbApi imdbApi;
    private LaunchSelectedMovie launchSelectedMovie;
    private CinemaBookingSystem cinemaBookingSystem;
    private CinemaDatabase cinemaDatabase;
    private Random random;


    public HomeController() {
        this.cinemaBookingSystem = Gui.getBookingSystemObject();
        imdbApi = new ImdbApi();
        launchSelectedMovie = new LaunchSelectedMovie();
        cinemaDatabase = new CinemaDatabase();
        random = new Random();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Sets up movies that are on the top list.
        List<List<String>> topListMovies = setTopListMovies();
        // Sets up the posters and actions when they are clicked on.
        posterAndPosterAction(topListMovies);
        // Selects a random movie from the list of top list movies.
        List<String> randomMovie = selectRandomMovie(topListMovies);
        // Set the trailer poster and the title for the current homepage movie.
        trailerPoster.setImage(new Image(randomMovie.get(2)));
        currentMovie.setText(randomMovie.get(0));
        // Sets up the trailer and the information needed if the user wants more info
        // about the showing homepage movie.
        setTrailerAndMovieInfo(randomMovie);

        //currentMovie = new Text(movies.get(randomFromList));
    }

    /**
     * Sets the top list movies and provides us with a list that has information about each top movie.
     *
     * @return a list containing lists which each has information about a specific movie.
     */
    private List<List<String>> setTopListMovies() {
        // Have to use double '' due to sql syntax.
        List<String> movie1Info = cinemaDatabase.getMovieInTheater("The King''s man");
        List<String> movie2Info = cinemaDatabase.getMovieInTheater("Black Widow");
        List<String> movie3Info = cinemaDatabase.getMovieInTheater("No Time to Die");
        List<String> movie4Info = cinemaDatabase.getMovieInTheater("Raya and the Last Dragon");
        List<String> movie5Info = cinemaDatabase.getMovieInTheater("Sing 2");

        return List.of(movie1Info, movie2Info, movie3Info, movie4Info, movie5Info);
    }

    /**
     * Selects a random movie from the list of displayNewMovies and returns information
     * about that movie.
     *
     * @param topListMovies a list containing other lists that each contains information about
     * a certain movie.
     * @return A list containin the information about the selected movie.
     */
    private List<String> selectRandomMovie(List<List<String>> topListMovies) {
        int randomFromList = random.nextInt(topListMovies.size());
        return topListMovies.get(randomFromList);
    }

    /**
     * Save for the movieInformationScene to set up for if the user wants more information
     * about the current homepage movie, such as screening time and tickets or if the user wants
     * to watch the trailer.
     *
     * @param randomMovie a random movie from the top list of movies.
     */
    private void setTrailerAndMovieInfo(List<String> randomMovie) {
        cinemaBookingSystem.setMovie(randomMovie.get(0));
        cinemaBookingSystem.setYear(Integer.parseInt(randomMovie.get(1)));
        cinemaBookingSystem.setTrailerImage(randomMovie.get(2));
        cinemaBookingSystem.setTrailer(randomMovie.get(3));
    }

    /**
     * Sets a poster for each of the top list movies and adds functionality to them,
     * so that it redirects to a new scene for further information that will be displayed to the user
     * about a movie when a poster is clicked on.
     *
     * @param topListMovies a list containing other list that each contains information about
     * a certain movie.
     */
    private void posterAndPosterAction(List<List<String>> topListMovies) {
        int movie = 0;
        List<ImageView> posters = List.of(firstPoster, secondPoster, thirdPoster, fourthPoster, fifthPoster);
        for (ImageView poster : posters) {
            var topListMovie = topListMovies.get(movie);
            poster.setImage(new Image(imdbApi.getPoster(topListMovie.get(0),
                    Integer.parseInt(topListMovie.get(1)))));

            poster.setOnMouseClicked(e -> launchSelectedMovie.selectedMovie(topListMovie.get(0),
                    Integer.parseInt(topListMovie.get(1)), topListMovie.get(2), topListMovie.get(3)));

            movie++;
        }
    }

    /**
     * Redirects the user to the Trailer Scene.
     *
     * @param mouseEvent Mouse event represents a click on the play button.
     */
    public void play(MouseEvent mouseEvent) throws Exception{
        cinemaBookingSystem.setPreviousScene("/scenes/HomeScene.fxml");
        SceneCreator.launchScene("/scenes/TrailerScene.fxml");
    }

    /**
     * Redirects the user to the Login Scene.
     *
     * @param mouseEvent Mouse event representing a click on the logout button.
     * @throws Exception
     */
    public void logout(MouseEvent mouseEvent) throws Exception{
        SceneCreator.launchScene("/scenes/LoginScene.fxml");
    }

    /**
     * Redirects the user to the Movies Scene.
     *
     * @param mouseEvent Mouse event representing a click on the movies and trailers button.
     * @throws Exception
     */
    public void openMovies(MouseEvent mouseEvent) throws Exception{
        SceneCreator.launchScene("/scenes/MoviesScene.fxml");
    }


    /**
     * Redirects the user to the Movie Information Scene.
     *
     * @param mouseEvent Represents a click on either movie displayed to the user.
     * @throws Exception
     */
    public void openMovieInformation(MouseEvent mouseEvent) throws Exception {
        SceneCreator.launchScene("/scenes/MovieInformationScene.fxml");
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
    public void openMyReservations(MouseEvent mouseEvent) throws Exception {
        SceneCreator.launchScene("/scenes/ReservationsScene.fxml");
    }
}