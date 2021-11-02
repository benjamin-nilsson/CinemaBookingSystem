package cinemabookingsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Establish a connection with a database
 *
 * @author Benjamin Nilsson
 * @since 31.08.2021
 */
public class CinemaDatabaseConnection {
    private Connection dataBaseLink;

    /**
     * @return a connection with the specified database
     */
    public Connection getConnectionUser() {
        String databaseName = "cinema-db-user";
        String databaseUser = "root";
        String databasePassword = "kifrjude0912";
        String url = "jdbc:mysql://localhost/" + databaseName;

        connectToDatabase(url, databaseUser, databasePassword);

        return dataBaseLink;
    }

    /**
     * @return a connection with the specified database
     */
    public Connection getConnectionBookings() {
        String databaseName = "cinema-db-bookings";
        String databaseUser = "root";
        String databasePassword = "kifrjude0912";
        String url = "jdbc:mysql://localhost/" + databaseName;

        connectToDatabase(url, databaseUser, databasePassword);

        return dataBaseLink;
    }

    /**
     * @return a connection with the specified database
     */
    public Connection getConnectionMovies() {
        String databaseName = "cinema-db-movies";
        String databaseUser = "root";
        String databasePassword = "kifrjude0912";
        String url = "jdbc:mysql://localhost/" + databaseName;

        connectToDatabase(url, databaseUser, databasePassword);

        return dataBaseLink;
    }

    /**
     * Establish a connection with the given database.
     * @param url the location of the database.
     * @param user the username for the database.
     * @param password the password for the database.
     */
    private void connectToDatabase(String url, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dataBaseLink = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
