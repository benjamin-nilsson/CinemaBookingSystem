package cinemabookingsystem.controllers;

import cinemabookingsystem.bookingsystem.CinemaBookingSystem;
import cinemabookingsystem.database.CinemaDatabase;
import cinemabookingsystem.gui.Gui;
import cinemabookingsystem.gui.SceneCreator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller for the Account Scene.
 * Shows the user his/her account information
 *
 * @author Benjamin Nilsson
 * @since 31.08.2021
 */
public class AccountController implements Initializable {

    @FXML
    private TextField emailBox, usernameBox, firstNameBox, lastNameBox;

    private CinemaDatabase cinemaDatabase;
    private CinemaBookingSystem cinemaBookingSystem;

    public AccountController() {
        cinemaDatabase = new CinemaDatabase();
        this.cinemaBookingSystem = Gui.getBookingSystemObject();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // set the fields in the cinemaDatabase class to be able to retrieve information
            // about the user's email, username, first- and lastname
            cinemaDatabase.getMemberInfo(cinemaBookingSystem.getUser());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // get and set the retrieved information for the user
        emailBox.setText(cinemaDatabase.getEmail());
        usernameBox.setText(cinemaDatabase.getDatabaseUsername());
        firstNameBox.setText(cinemaDatabase.getFirstName());
        lastNameBox.setText(cinemaDatabase.getLastName());
    }

    /**
     * Redirects the user to the Login Scene.
     *
     * @param event Mouse event representing a click on the logout button.
     * @throws Exception
     */
    public void logout(MouseEvent event) throws Exception{
        SceneCreator.launchScene("/scenes/LoginScene.fxml");
    }

    /**
     * Redirects the user to the Home Scene.
     *
     * @param event Mouse event representing a click on the logo button.
     * @throws Exception
     */
    public void openHome(MouseEvent event) throws Exception {
        SceneCreator.launchScene("/scenes/HomeScene.fxml");
    }

    /**
     * Redirects the user to the Edit Account Scene.
     *
     * @param event Mouse event representing a click on the edit account button.
     * @throws Exception
     */
    public void openEdit(MouseEvent event) throws Exception {
        SceneCreator.launchScene("/scenes/EditAccountScene.fxml");
    }

    /**
     * Redirects the user to the Movies Scene.
     *
     * @param event Mouse event representing a click on the movies and trailers button.
     * @throws Exception
     */
    public void openMovies(MouseEvent event) throws Exception {
        SceneCreator.launchScene("/scenes/MoviesScene.fxml");
    }

    /**
     * Redirects the user to My Reservations Scene.
     *
     * @param event Mouse event representing a click on my reservations button.
     * @throws Exception
     */
    public void openMyReservations(MouseEvent event) throws Exception{
        SceneCreator.launchScene("/scenes/ReservationsScene.fxml");
    }
}
