package cinemabookingsystem.controllers;

import cinemabookingsystem.bookingsystem.CinemaBookingSystem;
import cinemabookingsystem.database.CinemaDatabase;
import cinemabookingsystem.gui.Gui;
import cinemabookingsystem.gui.SceneCreator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Displays the reservation to the user if the user has one.
 * It also provides the user with the option to cancel the reservation.
 *
 * @author Benjamin Nilsson
 * @since 31.08.2021
 */
public class ReservationsController implements Initializable {
    @FXML
    private Text movieTitleText, seatsText, dateAndTimeText, cancelButton;

    private CinemaDatabase cinemaDatabase;
    private CinemaBookingSystem cinemaBookingSystem;

    public ReservationsController() {
        cinemaDatabase = new CinemaDatabase();
        this.cinemaBookingSystem = Gui.getBookingSystemObject();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // displays nothing if the user has no reservations
            if (cinemaDatabase.getUserReservations(cinemaBookingSystem.getUser()).isEmpty()) {
                seatsText.setText("");
                dateAndTimeText.setText("");
                cancelButton.setVisible(false);
            }
            else {
                // displays the users reservation information such as the movie, the seats,
                // and time and date of the screening
                seatsText.setText(cinemaDatabase.getUserReservations(cinemaBookingSystem.getUser()));
                movieTitleText.setText(cinemaDatabase.getMovieTitle());
                dateAndTimeText.setText(cinemaDatabase.getDate() + ", " + cinemaDatabase.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Enables the user to cancel its reservation for a movie
     *
     * @param mouseEvent Mouse event represents a click on the cancel reservation button
     * @throws Exception
     */
    public void cancelReservation(MouseEvent mouseEvent) throws Exception {
        // the user gets the option to either stop or continue with the cancellation of the reservation
        var alert = new Alert(Alert.AlertType.NONE, "Are you sure you want to cancel the reservation?", ButtonType.YES, ButtonType.NO);
        alertStyle(alert);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.NO) {
            alert.close();
        }
        else {
            // deletes the users reservation information from the database
            cinemaDatabase.removeReservation(cinemaBookingSystem.getUser());

            var completion = new Alert(Alert.AlertType.NONE, "Your reservation was cancelled successfully!", ButtonType.OK);

            // sets the style for the alert window
            alertStyle(completion);

            completion.showAndWait();

            if (alert.getResult() == ButtonType.OK)
                completion.close();

            // redirects the user back to the Reservation Scene to show that the user has no reservations
            SceneCreator.launchScene("/scenes/ReservationsScene.fxml");
        }
    }

    /**
     * Sets a determined style for the alert window.
     *
     * @param alert takes an Alert object
     */
    private void alertStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #000000;");
        dialogPane.lookup(".content.label").setStyle("-fx-font-size: 12px; "
                + "-fx-font-weight: bold; -fx-text-fill: #ffffff;");

        ButtonBar buttonBar = (ButtonBar)alert.getDialogPane().lookup(".button-bar");
        buttonBar.getButtons().forEach(b -> b.setStyle("-fx-background-color: #a51414;" +
                "-fx-text-fill: #ffffff;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor:hand;"));
    }

    /**
     * redirects the user to the Home Scene
     *
     * @param mouseEvent Mouse event representing a click on the logo button
     * @throws Exception
     */
    public void openHome(MouseEvent mouseEvent) throws Exception {
        SceneCreator.launchScene("/scenes/HomeScene.fxml");
    }

    /**
     * redirects the user to the Movies Scene
     *
     * @param mouseEvent Mouse event representing a click on the movies and trailers button
     * @throws Exception
     */
    public void openMovies(MouseEvent mouseEvent) throws Exception {
        SceneCreator.launchScene("/scenes/MoviesScene.fxml");
    }

    /**
     * redirects the user to the Login Scene
     *
     * @param mouseEvent Mouse event representing a click on the logout button
     * @throws Exception
     */
    public void logout(MouseEvent mouseEvent) throws Exception {
        SceneCreator.launchScene("/scenes/LoginScene.fxml");
    }

    /**
     * redirects the user to the Account Scene
     *
     * @param mouseEvent Mouse event representing a click on the My Account button
     * @throws Exception
     */
    public void openAccount(MouseEvent mouseEvent) throws Exception {
        SceneCreator.launchScene("/scenes/AccountScene.fxml");
    }
}
