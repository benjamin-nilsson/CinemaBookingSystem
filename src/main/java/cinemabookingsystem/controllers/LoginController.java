package cinemabookingsystem.controllers;

import cinemabookingsystem.bookingsystem.CinemaBookingSystem;
import cinemabookingsystem.database.CinemaDatabase;
import cinemabookingsystem.gui.Gui;
import cinemabookingsystem.gui.SceneCreator;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Check to see if the user has an account and if so, then login the user to the platform.
 * It also allows for the user to create an account.
 *
 * @author Benjamin Nilsson
 * @since 31.08.2021
 */
public class LoginController implements Initializable {
    private CinemaDatabase cinemaDatabase;

    @FXML
    private TextField usernameBox;

    @FXML
    private PasswordField passwordBox;

    @FXML
    private Button loginButton;

    @FXML
    private Label wrongUsernameOrPassword;

    private CinemaBookingSystem cinemaBookingSystem;

    public LoginController() {
        this.cinemaBookingSystem = Gui.getBookingSystemObject();
        cinemaDatabase = new CinemaDatabase();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        requiredFieldsAreFilled();
    }

    /**
     * Makes the user unable to log in if either field is left empty.
     */
    private void requiredFieldsAreFilled() {
        loginButton.disableProperty().bind(
                Bindings.isEmpty(usernameBox.textProperty())
                        .or(Bindings.isEmpty(passwordBox.textProperty()))
        );
    }

    /**
     * Redirects the user to the Create an Account Scene.
     *
     * @param actionEvent Action event represents a click on the create account button.
     * @throws Exception
     */
    public void createNewAccount(ActionEvent actionEvent) throws Exception{
        SceneCreator.launchScene("/scenes/CreateAnAccountScene.fxml");
    }

    /**
     * Makes sure the login information is correct and matches the user, and if correct
     * redirects the user to the Home Scene.
     *
     * @param actionEvent Action event represents a click on the login button.
     * @throws Exception
     */
    public void login(ActionEvent actionEvent) throws Exception{

        // checks to see if the password matches the username held in the database
        if (!passwordMatchesUser()) {
            wrongUsernameOrPassword.setVisible(true);
            return;
        }

        // sets up the user for a login and redirects the user to the Home Scene
        cinemaBookingSystem.setUser(usernameBox.getText());
        SceneCreator.launchScene("/scenes/HomeScene.fxml");
    }

    /**
     * Compares the username the user wrote and the password the user wrote by checking
     * in the database to see if the password matches the username.
     *
     * @return shows whether the password that the user wrote matches the username.
     * @throws Exception
     */
    private boolean passwordMatchesUser() throws Exception {
        var passwordForUsername = cinemaDatabase.passwordMatchesUsername(usernameBox.getText());

        return passwordForUsername.equals(passwordBox.getText());
    }
}


