package cinemabookingsystem.gui;

import cinemabookingsystem.bookingsystem.CinemaBookingSystem;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Gui extends Application {
    private static Stage primaryStage;
    private static CinemaBookingSystem currentBookingSystem;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // setting up the login scene
            Parent root = FXMLLoader.load(getClass().getResource("/scenes/LoginScene.fxml"));
            Gui.primaryStage = primaryStage;
            Gui.primaryStage.setTitle("Cinema Booking System");
            var scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/scenes/style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void setBookingSystemObject(CinemaBookingSystem bookingSystem) {
        currentBookingSystem = bookingSystem;
    }

    public static CinemaBookingSystem getBookingSystemObject() {
        return currentBookingSystem;
    }

    /**
     * @return the stage where we are setting the scene
     */
    public static Stage getStage() {
        return primaryStage;
    }
}
