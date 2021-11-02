package cinemabookingsystem.controllers;

import cinemabookingsystem.bookingsystem.CinemaBookingSystem;
import cinemabookingsystem.database.CinemaDatabase;
import cinemabookingsystem.gui.Gui;
import cinemabookingsystem.gui.SceneCreator;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller for the Create Account Scene.
 * Enables a user to create an account which will be added to the
 * database in order for the user to get access to the booking system.
 *
 * @author Benjamin Nilsson
 * @since 31.08.2021
 */
public class CreateAccountController implements Initializable {

    @FXML
    private TextField emailBox, choseUsernameBox;

    @FXML
    private PasswordField chosePasswordBox, confirmPasswordBox;

    @FXML
    private Label pswrdDoesNotMatch, takenUsername;

    @FXML
    private Button createAnAccountButton;

    private CinemaBookingSystem cinemaBookingSystem;
    private CinemaDatabase cinemaDatabase;

    public CreateAccountController() {
        cinemaDatabase = new CinemaDatabase();
        this.cinemaBookingSystem = Gui.getBookingSystemObject();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // prevents the user from being able to create an account if the required boxes are not filled in
        allFieldsFilled();
    }

    /**
     * Prevents the user from being able to create an account if the required boxes are not filled in.
     */
    private void allFieldsFilled() {
        createAnAccountButton.disableProperty().bind(
                Bindings.isEmpty(emailBox.textProperty())
                        .or(Bindings.isEmpty(choseUsernameBox.textProperty()))
                        .or(Bindings.isEmpty(chosePasswordBox.textProperty()))
                        .or(Bindings.isEmpty(confirmPasswordBox.textProperty()))
        );
    }

    /**
     * Creates an account for the user which will be added to the database
     * and then logs in the user.
     *
     * @param actionEvent Action event represents a click on the Create Account button.
     * @throws Exception
     */
    public void createAndLogin(ActionEvent actionEvent) throws Exception {
        // prevents the user from being able to create an account if the username already exist
        // in the database
        var taken = existingUsername(choseUsernameBox.getText());
        if (taken) {
            takenUsername.setVisible(true);
            return;
        }

        // prevents the user from being able to create an account if the passwords don't match
        if (!passwordIsCorrect()) {
            pswrdDoesNotMatch.setVisible(true);
            return;
        }

        // Adds the user to the database of Cinema Booking System's users.
        cinemaDatabase.addUserToDatabase(
                choseUsernameBox.getText(),
                confirmPasswordBox.getText(),
                emailBox.getText());

        // Sets the Cinema Booking System user to the logged in user.
        cinemaBookingSystem.setUser(choseUsernameBox.getText());
        SceneCreator.launchScene("/scenes/HomeScene.fxml");
    }

    /**
     * @return true if the username is in the database
     * @throws Exception
     */
    private boolean existingUsername(String username) throws Exception {
        return cinemaDatabase.hasUsername(username);
    }

    /**
     * @return true if the chosen chosePasswordBox matches the confirmPasswordBox
     */
    private boolean passwordIsCorrect() {
        return confirmPasswordBox.getText().equals(chosePasswordBox.getText());
    }
}
