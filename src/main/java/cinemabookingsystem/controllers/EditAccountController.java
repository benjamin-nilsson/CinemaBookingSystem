package cinemabookingsystem.controllers;

import cinemabookingsystem.bookingsystem.CinemaBookingSystem;
import cinemabookingsystem.database.CinemaDatabase;
import cinemabookingsystem.gui.Gui;
import cinemabookingsystem.gui.SceneCreator;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller for the Edit Account Scene.
 * Enables the user to edit user information in the account and the database data
 * such as, email, username, firstname, and lastname.
 *
 * @author Benjamin Nilsson
 * @since 31.08.2021
 */
public class EditAccountController implements Initializable {
    @FXML
    private TextField emailBox, usernameBox, firstNameBox, lastNameBox;

    @FXML
    private Button okButton;

    private CinemaDatabase cinemaDatabase;
    private CinemaBookingSystem cinemaBookingSystem;

    public EditAccountController() {
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

        // If one of the required boxes are not filled in the user will be unable to update the account
        // information and the database data.
        okButton.disableProperty().bind(
                Bindings.isEmpty(usernameBox.textProperty())
                        .or(Bindings.isEmpty(emailBox.textProperty()))
        );
    }

    /**
     * Updates the user's account information and the database data to any the changes that the user have made.
     */
    public void editUserInfo() {
        try {
            // The information about the user is updated in the database.
            cinemaDatabase.editMemberInfo(
                    emailBox.getText(),
                    usernameBox.getText(),
                    firstNameBox.getText(),
                    lastNameBox.getText());

            // Ff the user changed its username we have to set it to this new username.
            cinemaBookingSystem.setUser(usernameBox.getText());
            SceneCreator.launchScene("/scenes/AccountScene.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * Redirects the user to the Movies Scene.
     *
     * @param mouseEvent Mouse event representing a click on the movies and trailers button.
     * @throws Exception
     */
    public void openMovies(MouseEvent mouseEvent) throws Exception {
        SceneCreator.launchScene("/scenes/MoviesScene.fxml");
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
