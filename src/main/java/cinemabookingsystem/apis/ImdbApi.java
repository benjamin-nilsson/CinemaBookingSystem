package cinemabookingsystem.apis;

import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * calls the OMDB API which holds IMDB.com information about for example movies and trailers
 *
 * @author Benjamin Nilsson
 * @since 31.08.2021
 */
public class ImdbApi {
    /**
     * @param movie that we want to get the poster for
     * @param year that the movie was released
     * @return the specified movie poster
     */
    public String getPoster(String movie, int year) {
        String movieTitle = URLEncoder.encode(movie);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = getHttpRequest(movieTitle, year, "");

        int responseCode = getResponseCode(client, request);

        return (responseCode > 200)
                ? "Unable to retrieve poster"
                : client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parsePoster)
                .join();
    }

    /**
     * @param movie that we want to get the poster for
     * @param year that the movie was released
     * @return the plot for the movie
     */
    public String getPlot(String movie, int year) {
        String movieTitle = URLEncoder.encode(movie);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = getHttpRequest(movieTitle, year, "");

        int responseCode = getResponseCode(client, request);

        return (responseCode > 200)
                ? "Unable to retrieve plot"
                : client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parsePlot)
                .join();
    }

    /**
     * @param movie that we want to get the poster for
     * @param year that the movie was released
     * @return the rating for the movie
     */
    public String getRating(String movie, int year) {
        String movieTitle = URLEncoder.encode(movie);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = getHttpRequest(movieTitle, year, "&plot=full");

        int responseCode = getResponseCode(client, request);

        return (responseCode > 200)
                ? "Unable to retrieve movie information"
                : client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseRating)
                .join();
    }

    /**
     * Creates a GET request and returns this request.
     * @param movieTitle the title of the move that we want to get the poster for
     * @param year that the movie was released
     * @param furtherSpecification the additional information we want go get from the movie, such as plot.
     * @return the request that we want to make, which in this case is a GET request.
     */
    private HttpRequest getHttpRequest(String movieTitle, int year, String furtherSpecification) {

        return HttpRequest.newBuilder(
                URI.create("http://www.omdbapi.com/?i=tt3896198&apikey=ba1855b1&t=" + movieTitle + "&y=" + year + furtherSpecification))
                .GET()
                .build();
    }

    /**
     * Tells us whether the request was successful or not.
     * @param client the client which will be used to send requests and retrieve their responses.
     * @param request the request that you want to perform(post, get, update, delete).
     * @return an integer representation that tells if a specific HTTP request has been successfully
     * completed (successful codes range from 200-299).
     */
    private int getResponseCode(HttpClient client, HttpRequest request) {

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::statusCode)
                .join();
    }

    /**
     * @param responseBody the information we get from the API call
     * @return the rating of the movie
     */
    private String parseRating(String responseBody) {
        JSONObject movieInformation = new JSONObject(responseBody);

        String genre = movieInformation.getString("Genre");
        String rated = movieInformation.getString("Rated");
        String runtime = movieInformation.getString("Runtime");

        return genre + "\n" + rated + "\n" + runtime;
    }

    /**
     * @param responseBody the information we get from the API call
     * @return the poster of the movie
     */
    private String parsePoster(String responseBody) {
        JSONObject movieInformation = new JSONObject(responseBody);

        return movieInformation.getString("Poster");
    }

    /**
     * @param responseBody the information we get from the API call
     * @return the plot of the movie
     */
    private String parsePlot(String responseBody) {
        JSONObject movieInformation = new JSONObject(responseBody);

        return movieInformation.getString("Plot");
    }
}
