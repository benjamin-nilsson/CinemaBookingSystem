package cinemabookingsystem.database;

import cinemabookingsystem.bookingsystem.CinemaBookingSystem;
import cinemabookingsystem.gui.Gui;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to register and retrieve data regarding a user, and a users reservation
 * as well as all the movies the cinema is showing and each movies information.
 *
 * @author Benjamin Nilsson
 * @since 31.08.2021
 */
public class CinemaDatabase {
    private String email, databaseUsername, firstName, lastName, movieTitle, date, time, password;
    private CinemaBookingSystem cinemaBookingSystem;

    public CinemaDatabase() {
        this.cinemaBookingSystem = Gui.getBookingSystemObject();
    }

    /**
     * Checks to see if the database contains the username provided.
     *
     * @param username the input that the user put in as username.
     * @return true if the database contains the name.
     * @throws Exception
     */
    public boolean hasUsername(String username) throws Exception {
        var connectDB = establishConnection();

        String connectQuery = "SELECT username FROM customers WHERE username LIKE '" + username + "'";

        ResultSet queryOutput = executeQuery(connectDB, connectQuery);

        while (queryOutput.next()) {
            databaseUsername = queryOutput.getString("username");
        }

        if (databaseUsername == null)
            return false;

        return databaseUsername.equals(username);
    }

    /**
     * Checks to see if the username provided is in the database and if so
     * returns the decrypted password of the user. Otherwise it returns an
     * empty String.
     *
     * @param username the input that the user put in as username.
     * @return an empty String if the password does not match user, else returns the username.
     * @throws Exception
     */
    public String passwordMatchesUsername(String username) throws Exception {
        var connectDB = establishConnection();

        String connectQuery = "SELECT AES_DECRYPT(password, 'secret') AS 'password' \n" +
                "FROM customers\n" +
                "WHERE username = '" + username + "'";

        ResultSet queryOutput = executeQuery(connectDB, connectQuery);

        while (queryOutput.next())
            password = queryOutput.getString("password");

        return (password == null) ? "" : password;
    }

    /**
     * Adds a user to the database containing all the Cinema Booking System's users.
     *
     * @param username the input that the user will user as username to login.
     * @param password the input that the user will use as password to login.
     * @param email of the user.
     */
    public void addUserToDatabase(String username, String password, String email) {
        var connectDB = establishConnection();

        String connectQuery =
                        "INSERT INTO customers VALUES ('" +
                        username + "', AES_ENCRYPT('" + password + "', 'secret'), '" + email + "', '', '', DEFAULT)";
        try {

            var statement = connectDB.createStatement();
            statement.execute(connectQuery);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Provides the specified fields with data regarding the specified user
     * such as username, email, firstname, lastname.
     *
     * @param username the input that the user put in as username.
     * @throws Exception
     */
    public void getMemberInfo(String username)  throws Exception {
        var connectDB = establishConnection();

        String connectQuery = "SELECT *" +
                "FROM customers\n" +
                "WHERE username = '" + username + "'";

        ResultSet queryOutput = executeQuery(connectDB, connectQuery);

        while (queryOutput.next()) {
            databaseUsername = queryOutput.getString("username");
            email = queryOutput.getString("email");
            firstName = queryOutput.getString("first_name");
            lastName = queryOutput.getString("last_name");
        }
    }

    /**
     * Allows the user to change its personal information specified when creating the account.
     *
     * @param email that the user has the ability to change.
     * @param username that the user has the ability to change.
     * @param firstName that the user has the ability to change.
     * @param lastName that the user has the ability to change.
     * @throws Exception
     */
    public void editMemberInfo(String email, String username, String firstName, String lastName) throws Exception {
        var connectDB = establishConnection();

        String connectQuery = "UPDATE customers " +
        "SET username = '" + username +
                "', email = '" + email +
                "', first_name = '" + firstName +
                "', last_name = '" + lastName +
                "' WHERE username = '" + cinemaBookingSystem.getUser() + "'";

        var statement = connectDB.createStatement();
        statement.executeUpdate(connectQuery);
    }

    /**
     * Puts information about who made the reservation, the seats that were selected,
     * and the time and date of when the screening of the movie is supposed to take place
     * and where the ticket that the user will get will be valid.
     *
     * @param movieTitle of the movie that the user wants to reserve seats for.
     * @param username of the user that is making the reservation.
     * @param firstSeat beginning of the user's selected seats.
     * @param lastSeat end of the user's selected seats.
     * @param movieYear the year the movie is being released.
     */
    public void reserveSeats(String movieTitle, String username, String firstSeat, String lastSeat, int movieYear) {
        var movie = movieTitle.replace("'", "´");

        var connectNow = new CinemaDatabaseConnection();
        var connectDB = connectNow.getConnectionBookings();

        String connectQuery =
                "INSERT INTO bookings VALUES ('" + movie + "', '" + username + "', '" + firstSeat + "', '"
                + lastSeat + "', '" + movieYear + "', + '" + cinemaBookingSystem.getDate() + "', '"
                + cinemaBookingSystem.getTime() + "')";
        try {
            var statement = connectDB.createStatement();
            statement.execute(connectQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Check to see if a user has a reservation.
     *
     * @return true if the user has a reservation in the database.
     * @throws SQLException
     */
    public boolean hasReservation() throws SQLException {
        var connectDB = establishConnection();

        String connectQuery =
                "SELECT username FROM `cinema-db-bookings`.bookings WHERE username = '"
                + cinemaBookingSystem.getUser() + "'";

        ResultSet queryOutput = executeQuery(connectDB, connectQuery);

        var username = "";
        while (queryOutput.next()) {
            username = queryOutput.getString("username");
        }

        return !username.isEmpty();
    }

    /**
     * Removes the user's previously made reservation.
     *
     * @param username the username of the user that is logged in.
     * @throws Exception
     */
    public void removeReservation(String username) throws Exception {
        var connectDB = establishConnection();

        String connectQuery = "DELETE FROM `cinema-db-bookings`.bookings WHERE username = '" + username + "'";

        var statement = connectDB.createStatement();
        statement.execute(connectQuery);
    }

    /**
     * @param movieTitle of the movie that the user has reserved.
     * @return a list of all the users first reserved seat for a specified movie, date, and time.
     * @throws Exception
     */
    public List<String> getFirstBookedSeats(String movieTitle) throws Exception {
        List<String> firstSeats = new ArrayList<>();
        var movie = movieTitle.replace("'", "´");

        var connectDB = establishConnection();

        String connectQuery =
                "SELECT firstSeat FROM `cinema-db-bookings`.bookings WHERE movieTitle = '" + movie
                + "' and dateOfScreening = '" + cinemaBookingSystem.getDate()
                + "' and timeOfScreening = '" + cinemaBookingSystem.getTime() + "'";

        ResultSet queryOutput = executeQuery(connectDB, connectQuery);

        while (queryOutput.next()) {
            var seats = queryOutput.getString("firstSeat");
            firstSeats.add(seats);
        }

        return firstSeats;
    }

    /**
     * @param movieTitle of the movie that the user has reserved.
     * @return a list of all the users last reserved seat for a specified movie, date, and time.
     * @throws Exception
     */
    public List<String> getLastBookedSeats(String movieTitle) throws Exception {
        List<String> lastSeats = new ArrayList<>();
        var movie = movieTitle.replace("'", "´");

        var connectDB = establishConnection();

        String connectQuery =
                "SELECT lastSeat FROM `cinema-db-bookings`.bookings WHERE movieTitle = '" + movie
                + "' and dateOfScreening = '" + cinemaBookingSystem.getDate()
                + "' and timeOfScreening = '" + cinemaBookingSystem.getTime() + "'";

        ResultSet queryOutput = executeQuery(connectDB, connectQuery);

        while (queryOutput.next()) {
            var seats = queryOutput.getString("lastSeat");
            lastSeats.add(seats);
        }

        return lastSeats;
    }

    /**
     * @param username the username of the user that is logged in.
     * @return information about which movie the user is going to see, the user's seats, and the time and date
     * the screening will take place.
     * @throws Exception
     */
    public String getUserReservations(String username) throws Exception {
        var connectDB = establishConnection();

        String connectQuery = "SELECT * FROM `cinema-db-bookings`.bookings WHERE username = '" + username + "'";

        ResultSet queryOutput = executeQuery(connectDB, connectQuery);

        var seats = "";
        while (queryOutput.next()) {
            seats = queryOutput.getString("firstSeat") + " - "
                    + queryOutput.getString("lastSeat");
            movieTitle = queryOutput.getString("movieTitle");
            date = queryOutput.getString("dateOfScreening");
            time = queryOutput.getString("timeOfScreening");
        }

        return seats;
    }

    /**
     * Provides a list containing a movies information such as, title, the year of the movie,
     * the path to the poser and the path the the trailer.
     *
     * @param movie the movie that we want to get information from.
     * @return a list containing information about a movie that is showing.
     * @throws Exception
     */
    public List<String> getMovieInTheater(String movie) {
        ArrayList<String> movieInformation = new ArrayList<>();


        var connectNow = new CinemaDatabaseConnection();
        var connectDB = connectNow.getConnectionMovies();

        String connectQuery = "SELECT * FROM `cinema-db-movies`.movies_in_theater WHERE movie = '" + movie + "'";

        try {
            ResultSet queryOutput = executeQuery(connectDB, connectQuery);

            while (queryOutput.next()) {
                movieInformation.add(queryOutput.getString("movie"));
                movieInformation.add(queryOutput.getString("movie_year"));
                movieInformation.add(queryOutput.getString("movie_poster_path"));
                movieInformation.add(queryOutput.getString("movie_trailer_path"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("The theater does not have this movie.");
        }

        return movieInformation;
    }

    /**
     * Provides all the movies titles in a list ordered by the year the movies came out.
     *
     * @return a list of all the movie titles.
     * @throws Exception
     */
    public List<String> getMoviesInTheater() {
        ArrayList<String> movieTitles = new ArrayList<>();

        var connectNow = new CinemaDatabaseConnection();
        var connectDB = connectNow.getConnectionMovies();

        String connectQuery = "SELECT * FROM `cinema-db-movies`.movies_in_theater ORDER BY movie_year DESC";

        try {
            ResultSet queryOutput = executeQuery(connectDB, connectQuery);

            while (queryOutput.next()) {
                movieTitles.add(queryOutput.getString("movie"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("The theater does not have this movie.");
        }

        return movieTitles;
    }

    /**
     * @return a connection with the selected database.
     */
    private Connection establishConnection() {
        var connectNow = new CinemaDatabaseConnection();
        return connectNow.getConnectionUser();
    }

    /**
     * Establishes a connection with a database and execute a query to the database
     * and returns the databases response.
     *
     * @param connectDB the connection with the database.
     * @param connectQuery the query we want to send to the database.
     * @return the result of the query.
     * @throws SQLException
     */
    private ResultSet executeQuery(Connection connectDB, String connectQuery) throws SQLException {
        var statement = connectDB.createStatement();
        return statement.executeQuery(connectQuery);
    }

    /**
     * @return the email stored in the database for the logged in user
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the username of user if the user is previously stored in the database
     */
    public String getDatabaseUsername() {
        return databaseUsername;
    }

    /**
     * @return the first name of the user that the user has put in
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the last name of the user that the user has put in
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return the title for a movie
     */
    public String getMovieTitle() {
        return movieTitle;
    }

    /**
     * @return the date of screening for a movie
     */
    public String getDate() {
        return date;
    }

    /**
     * @return a time of screening for a movie
     */
    public String getTime() {
        return time;
    }
}
