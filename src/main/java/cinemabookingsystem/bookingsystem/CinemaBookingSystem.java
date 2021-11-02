package cinemabookingsystem.bookingsystem;

import cinemabookingsystem.gui.Gui;
import javafx.application.Application;

public class CinemaBookingSystem {
    private String movieTitle, trailerImage, trailer, user;
    private int movieYear;
    private int numberOfTickets = 2;
    private int costOfTickets = 2;
    private String date = "";
    private String time = "";
    private String previousScene;

    public CinemaBookingSystem(String[] args) {
        main(args);
    }

    /**
     * Launches the first scene.
     * @param args
     */
    public void main(String[] args) {
        Gui.setBookingSystemObject(this);
        Application.launch(Gui.class, args);
    }

    /**
     * @param movie stores the movie that the user has clicked on
     */
    public void setMovie(String movie) {
        movieTitle = movie;
    }

    /**
     * @return the movie that the user clicked on
     */
    public String getMovie() {
        return movieTitle;
    }

    /**
     * @param image stores the trailer poster that the user has clicked on
     */
    public void setTrailerImage(String image) {
        trailerImage = image;
    }

    /**
     * @return the trailer poster that the user clicked on
     */
    public String getTrailerImage() {
        return trailerImage;
    }

    /**
     * @param movieTrailer stores the movie trailer for the movie that the user clicked on
     */
    public void setTrailer(String movieTrailer) {
        trailer = movieTrailer;
    }

    /**
     * @return the movie trailer for the movie that the user clicked on
     */
    public String getTrailer() {
        return trailer;
    }

    /**
     * @param year stores the year the movie was released for the movie that the user clicked on
     */
    public void setYear(int year) {
        movieYear = year;
    }

    /**
     * @return the year that the movie was released that the user clicked on
     */
    public int getYear() {
        return movieYear;
    }

    /**
     * @param username stores the user who is logged in
     */
    public void setUser(String username) {
        user = username;
    }

    /**
     * @return the user that is logged in
     */
    public String getUser() {
        return user;
    }

    /**
     * @param numberOfPeople stores the number of people that is going to watch the movie
     */
    public void setNumberOfTickets(int numberOfPeople) {
        numberOfTickets = numberOfPeople;
    }

    /**
     * @return the number of people that is going to watch the movie
     */
    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    /**
     * @param numberOfPeople stores the cost of the tickets
     */
    public void setCostOfTickets(int numberOfPeople) {
        costOfTickets = numberOfPeople;
    }

    /**
     * @return the cost of the tickets
     */
    public int getCostOfTickets() {
        return costOfTickets;
    }

    /**
     * @param date stores the date that the user selected for screening
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the date that the user selected for screening
     */
    public String getDate() {
        return date;
    }

    /**
     * @param time stores the time that the user selected for screening
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return the time that the user selected for screening
     */
    public String getTime() {
        return time;
    }

    /**
     * @return the scene that the user was before the current scene.
     */
    public String getPreviousScene() {
        return previousScene;
    }

    /**
     * Sets the scene that will be the previous scene.
     * @param previousScene the scene that will be the previous scene.
     */
    public void setPreviousScene(String previousScene) {
        this.previousScene = previousScene;
    }
}