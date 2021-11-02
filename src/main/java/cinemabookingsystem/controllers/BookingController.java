package cinemabookingsystem.controllers;

import cinemabookingsystem.apis.ImdbApi;
import cinemabookingsystem.bookingsystem.CinemaBookingSystem;
import cinemabookingsystem.database.CinemaDatabase;
import cinemabookingsystem.gui.Gui;
import cinemabookingsystem.gui.SceneCreator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * The controller for the Booking Scene.
 * Used to get the the seats for a movie at a specific time and date where the user is
 * able to go through and select seats for the screening.
 *
 * @author Benjamin Nilsson
 * @since 31.08.2021
 */
public class BookingController implements Initializable {

    @FXML
    private FontAwesomeIconView A1, A2, A3, A4, A5, A6, A7, A8,
                                B1, B2, B3, B4, B5, B6, B7, B8, B9, B10,
                                C1, C2, C3, C4, C5, C6, C7, C8, C9, C10,
                                D1, D2, D3, D4, D5, D6, D7, D8, D9, D10,
                                E1, E2, E3, E4, E5, E6, E7, E8, E9, E10;

    @FXML
    private Text chosenSeatText, numberOfPeopleText, movieTitleText, costText, dateText;

    @FXML
    private ImageView moviePoster;

    private CinemaDatabase cinemaDatabase;
    private ImdbApi imdbApi;
    private int numberOfPeople;
    private int firstSeatNumber;
    private int lastSeatNumber;
    private List<FontAwesomeIconView> cinemaSeats, unavailableSeats;
    private final List<FontAwesomeIconView> couchSeats, wheelchairSeats;
    private final List<Integer> notAvailableSeatsIfTwo, notAvailableSeatsIfThree;
    private List<String> beginningOfSeats, endOfSeats;
    private Random random;
    private CinemaBookingSystem cinemaBookingSystem;

    public BookingController() {
        this.cinemaBookingSystem = Gui.getBookingSystemObject();
        numberOfPeople = cinemaBookingSystem.getNumberOfTickets();
        notAvailableSeatsIfTwo = List.of(7, 17, 27, 37, 47);
        notAvailableSeatsIfThree = List.of(6, 16, 26, 36, 46);
        cinemaDatabase = new CinemaDatabase();
        imdbApi = new ImdbApi();
        cinemaSeats = new ArrayList<>();
        beginningOfSeats = new ArrayList<>();
        endOfSeats = new ArrayList<>();
        unavailableSeats = new ArrayList<>();
        couchSeats = new ArrayList<>();
        wheelchairSeats = new ArrayList<>();
        random = new Random();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* set the information about the chosen movie, the number of tickets, the cost of tickets
         and the date of the chosen screening*/
        numberOfPeopleText.setText(String.valueOf(cinemaBookingSystem.getNumberOfTickets()));
        costText.setText(cinemaBookingSystem.getCostOfTickets() + "$");
        var selectedMovie = cinemaBookingSystem.getMovie();
        moviePoster.setImage(new Image(imdbApi.getPoster(selectedMovie, cinemaBookingSystem.getYear())));
        movieTitleText.setText(selectedMovie);
        dateText.setText(cinemaBookingSystem.getDate() + ", " + cinemaBookingSystem.getTime());



        // set the different colors that will be used to fill the seats depending on
        // which kind of seat it is(couch, wheelchair, ordinary) and if it is available or not
        var selectedColor = Color.valueOf("#ffffff");
        var unselectedColor = Color.valueOf("#7c7272");
        var couchColor = Color.valueOf("#5e0d8a");
        var wheelchairColor = Color.valueOf("#1010e4");
        var reservedColor = Color.valueOf("#1e1c1c");
        String unavailableColor = "0x1e1c1cff";

        // add all the seats to the cinemaSeats list
        setAllSeats();
        setReservedSeats(reservedColor);
        setCouchAndWheelChairSeats();
        setRecommendedSeats();

        // goes through all the seats
        for (var i = 0; i < cinemaSeats.size(); i++) {
            var seatOne = i;
            var seatTwo = i + 1;
            var seatThree = i + 2;

            highlightHoveredSeats(seatOne, seatTwo, seatThree, selectedColor, unavailableColor);

            removeHoveringEffectsOnExit(
                    seatOne, seatTwo, seatThree, wheelchairColor, couchColor,
                    unavailableColor, unselectedColor, selectedColor);

            markSelectedSeats(
                    seatOne, seatTwo, seatThree, wheelchairColor, couchColor,
                    unavailableColor, reservedColor, unselectedColor, selectedColor);
        }
    }

    /**
     * Sets the list for cinemaSeats with all the seats.
     */
    private void setAllSeats() {
        cinemaSeats = List.of(A1, A2, A3, A4, A5, A6, A7, A8,
                B1, B2, B3, B4, B5, B6, B7, B8, B9, B10,
                C1, C2, C3, C4, C5, C6, C7, C8, C9, C10,
                D1, D2, D3, D4, D5, D6, D7, D8, D9, D10,
                E1, E2, E3, E4, E5, E6, E7, E8, E9, E10
        );
    }

    /**
     * Retrieves the booked seats from the database and sets these seats
     * to the reserved color.
     */
    private void setReservedSeats(Color reservedColor) {
        try {
            // as the database reads the "'" character as syntax we need to replace the
            // character with another one
            var movie = cinemaBookingSystem.getMovie().replace("'", "´");

            // get the already reserved seats from the database of a movie at the chosen screening time
            // and set these seats to the color unavailable for the user
            beginningOfSeats = cinemaDatabase.getFirstBookedSeats(movie);
            endOfSeats = cinemaDatabase.getLastBookedSeats(movie);
            for (var i = 0; i < beginningOfSeats.size(); i++) {
                for (var j = 0; j < cinemaSeats.size(); j++) {
                    var start = beginningOfSeats.get(i);
                    var end = endOfSeats.get(i);
                    if (cinemaSeats.get(j).getId().equals(start)) {
                        cinemaSeats.get(j).setFill(reservedColor);
                        unavailableSeats.add(cinemaSeats.get(j));
                        while (!end.equals("") && !cinemaSeats.get(j).getId().equals(end)) {
                            cinemaSeats.get(j++).setFill(reservedColor);
                            unavailableSeats.add(cinemaSeats.get(j));
                        }
                    }
                    if (cinemaSeats.get(j).getId().equals(end)) {
                        cinemaSeats.get(j).setFill(reservedColor);
                        unavailableSeats.add(cinemaSeats.get(j));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the seats that we want for the couches and wheelchairs to their lists.
     */
    private void setCouchAndWheelChairSeats() {
        // get the couch seats and add them to the couchSeats list
        for (var i = 28; i < 48; i++)
            couchSeats.add(cinemaSeats.get(i));

        // get the wheelchair seats and add them to the wheelChairSeats list
        // and also add them to the unavailableSeats list
        for (var i = 5; i < 8; i++) {
            wheelchairSeats.add(cinemaSeats.get(i));
            unavailableSeats.add(cinemaSeats.get(i));
        }
    }

    /**
     * Marks random seats as selected sets and highlights these.
     */
    private void setRecommendedSeats() {
        // get a random number that will be used to initialize our first- and lastSeat and recommend a seat to the user
        var randomSeat = random.nextInt(45);

        /* Prevent random from selecting booked and/or wheelchair seats as a suggestion
         as well as seats that are split up by row.
         A problem will occur if there are too many seats taken so that the randomSeat cannot get its space
        */
        while (unavailableSeats.contains(cinemaSeats.get(randomSeat))
                || unavailableSeats.contains(cinemaSeats.get(randomSeat +1))
                || unavailableSeats.contains(cinemaSeats.get(randomSeat +2))
                || notAvailableSeatsIfTwo.contains(randomSeat)
                || notAvailableSeatsIfThree.contains(randomSeat)) {
            randomSeat = random.nextInt(45);
        }

        // one reason for having recommending seats is to to ensure that seat A1 doesn't get auto-filled
        // when running the program as the variables have to be initialized
        firstSeatNumber = randomSeat;
        lastSeatNumber = randomSeat +1;

        // set the color of the recommended seats, the number of seats recommended depends on number of people selected
        fillRecommendedSeats(
                cinemaSeats, notAvailableSeatsIfTwo, notAvailableSeatsIfThree,
                numberOfPeople, chosenSeatText, firstSeatNumber, lastSeatNumber);
    }

    /**
     * Highlight seats that are being hovered unless they are unavailable or would make the user
     * split seats between rows.
     *
     * @param seatOne the first seat that is selected.
     * @param seatTwo the seat that comes after the first seat.
     * @param seatThree the seat that comes after the second seat.
     * @param selectedColor the selected color of the seats.
     * @param unavailableColor the reserved color of the seats as a String representation.
     */
    private void highlightHoveredSeats(
            int seatOne, int seatTwo, int seatThree, Color selectedColor, String unavailableColor) {
            /* Unless the seats is already reserved we will change the color of the seat that the mouse
             is hovering to the selected color and depending on number of people, seats in front of the
             hovered seat will change to the selected color as well.
             When the user want to book more than one ticket it ensures that the user cannot book seats
             that would make us split the seats between two rows or go beyond the number of seats we have available.
            */
        if (!cinemaSeats.get(seatOne).getFill().toString().equals(unavailableColor)) {
            cinemaSeats.get(seatOne).setOnMouseEntered(e -> {
                if (numberOfPeople == 1)
                    cinemaSeats.get(seatOne).setFill(selectedColor);

                else if (numberOfPeople == 2
                        && !notAvailableSeatsIfTwo.contains(seatOne)
                        && !cinemaSeats.get(seatTwo).getFill().toString().equals(unavailableColor)) {
                    cinemaSeats.get(seatOne).setFill(selectedColor);
                    cinemaSeats.get(seatTwo).setFill(selectedColor);
                }

                else if (numberOfPeople == 3
                        && !notAvailableSeatsIfTwo.contains(seatOne)
                        && !notAvailableSeatsIfThree.contains(seatOne)
                        && !cinemaSeats.get(seatThree).getFill().toString().equals(unavailableColor)) {
                    cinemaSeats.get(seatOne).setFill(selectedColor);
                    cinemaSeats.get(seatTwo).setFill(selectedColor);
                    cinemaSeats.get(seatThree).setFill(selectedColor);
                }
            });
        }
    }

    /**
     * Removes the highlighted color of the seats when they are no longer hovered upon and set them to their
     * assigned color.
     *
     * @param seatOne the first seat that is selected.
     * @param seatTwo the seat that comes after the first seat.
     * @param seatThree the seat that comes after the second seat.
     * @param wheelchairColor the color of the wheelchair seats.
     * @param couchColor the color of the couch seats.
     * @param unavailableColor the reserved color of the seats as a String representation.
     * @param unselectedColor the normal color of the seats.
     * @param selectedColor the selected color of the seats.
     */
    private void removeHoveringEffectsOnExit(
            int seatOne, int seatTwo, int seatThree, Color wheelchairColor, Color couchColor,
            String unavailableColor, Color unselectedColor, Color selectedColor) {
        /* Depending on the number of people and and how we are moving the mouse(forwards, backwards, downwards,
             or upwards) the seat/seats will change to its original color when mouse is moved away from that
             seat(not hovered anymore).
             We also make sure so that seats that are already unavailable doesn't change color
            */
        if (!cinemaSeats.get(seatOne).getFill().toString().equals(unavailableColor)) {
            cinemaSeats.get(seatOne).setOnMouseExited(e -> {
                if (numberOfPeople == 1) {
                    var seatI = cinemaSeats.get(seatOne);
                    // make sure the seats go back to its original color when left,
                    // so couch goes back to couch, wheelchair to wheelchair etc
                    if (couchSeats.contains(seatI))
                        cinemaSeats.get(seatOne).setFill(couchColor);
                    else if (wheelchairSeats.contains(seatI))
                        cinemaSeats.get(seatOne).setFill(wheelchairColor);
                    else
                        cinemaSeats.get(seatOne).setFill(unselectedColor);

                    // make sure that if we have selected seats by clicking on them that they won't be unselected
                    // by the hovering effect
                    var first = cinemaSeats.get(firstSeatNumber);
                    cinemaSeats.forEach(item -> {
                        if (item == first)
                            item.setFill(selectedColor);
                    });
                } else if (numberOfPeople == 2
                        && !notAvailableSeatsIfTwo.contains(seatOne)
                        && !cinemaSeats.get(seatTwo).getFill().toString().equals(unavailableColor)) {
                    var seatI = cinemaSeats.get(seatOne);
                    var seatII = cinemaSeats.get(seatTwo);
                             /* Make sure the seats go back to its original color when left,
                             so couch goes back to couch, wheelchair to wheelchair etc.
                             If one of them is a couch seat, then all of them are.
                             */
                    if (couchSeats.contains(seatI)) {
                        cinemaSeats.get(seatOne).setFill(couchColor);
                        cinemaSeats.get(seatTwo).setFill(couchColor);
                    } else if (wheelchairSeats.contains(seatI)) {
                        cinemaSeats.get(seatOne).setFill(wheelchairColor);
                        cinemaSeats.get(seatTwo).setFill(wheelchairColor);
                    } else if (wheelchairSeats.contains(seatII)) {
                        cinemaSeats.get(seatOne).setFill(unselectedColor);
                        cinemaSeats.get(seatTwo).setFill(wheelchairColor);
                    } else {
                        cinemaSeats.get(seatOne).setFill(unselectedColor);
                        cinemaSeats.get(seatTwo).setFill(unselectedColor);
                    }

                    // make sure that if we have selected seats by clicking on them that they won't be unselected
                    // by the hovering effect
                    var first = cinemaSeats.get(firstSeatNumber);
                    var second = cinemaSeats.get(lastSeatNumber);
                    cinemaSeats.forEach(item -> {
                        if (item == first || item == second)
                            item.setFill(selectedColor);
                    });
                } else if (numberOfPeople == 3
                        && !notAvailableSeatsIfThree.contains(seatOne)
                        && !notAvailableSeatsIfTwo.contains(seatOne)
                        && !cinemaSeats.get(seatThree).getFill().toString().equals(unavailableColor)) {
                    var seatI = cinemaSeats.get(seatOne);
                    var seatII = cinemaSeats.get(seatTwo);
                    var seatIII = cinemaSeats.get(seatThree);

                          /* Make sure the seats go back to its original color when left,
                             so couch goes back to couch, wheelchair to wheelchair etc.
                             If one of them is a couch seat, then all of them are.
                           */
                    if (couchSeats.contains(seatI)) {
                        cinemaSeats.get(seatOne).setFill(couchColor);
                        cinemaSeats.get(seatTwo).setFill(couchColor);
                        cinemaSeats.get(seatThree).setFill(couchColor);
                    } else if (wheelchairSeats.contains(seatI)) {
                        cinemaSeats.get(seatOne).setFill(wheelchairColor);
                        cinemaSeats.get(seatTwo).setFill(wheelchairColor);
                        cinemaSeats.get(seatThree).setFill(wheelchairColor);
                    } else if (wheelchairSeats.contains(seatII)) {
                        cinemaSeats.get(seatOne).setFill(unselectedColor);
                        cinemaSeats.get(seatTwo).setFill(wheelchairColor);
                        cinemaSeats.get(seatThree).setFill(wheelchairColor);
                    } else if (wheelchairSeats.contains(seatIII)) {
                        cinemaSeats.get(seatOne).setFill(unselectedColor);
                        cinemaSeats.get(seatTwo).setFill(unselectedColor);
                        cinemaSeats.get(seatThree).setFill(wheelchairColor);
                    } else {
                        cinemaSeats.get(seatOne).setFill(unselectedColor);
                        cinemaSeats.get(seatTwo).setFill(unselectedColor);
                        cinemaSeats.get(seatThree).setFill(unselectedColor);
                    }

                    // make sure that if we have selected seats by clicking on them that they won't be unselected
                    // regardless if we move the mouse over or leave the seat/seats with the mouse.
                    var first = cinemaSeats.get(firstSeatNumber);
                    var second = cinemaSeats.get(firstSeatNumber + 1);
                    var third = cinemaSeats.get(lastSeatNumber);
                    cinemaSeats.forEach(item -> {
                        if (item == first || item == second || item == third)
                            item.setFill(selectedColor);
                    });
                }
            });
        }
    }

    /**
     * Marks the seats that are highlighted with the selected color and ensure that it stays
     * highlighted until other seats all selected.
     * Sets all other seats to their assigned colors.
     *
     * @param seatOne the first seat that is selected.
     * @param seatTwo the seat that comes after the first seat.
     * @param seatThree the seat that comes after the second seat.
     * @param wheelchairColor the color of the wheelchair seats.
     * @param couchColor the color of the couch seats.
     * @param unavailableColor the reserved color of the seats as a String representation.
     * @param reservedColor the reserved color of the seats.
     * @param unselectedColor the normal color of the seats.
     * @param selectedColor the selected color of the seats.
     */
    private void markSelectedSeats(
            int seatOne, int seatTwo, int seatThree, Color wheelchairColor, Color couchColor,
            String unavailableColor, Color reservedColor, Color unselectedColor, Color selectedColor) {
        if (!cinemaSeats.get(seatOne).getFill().toString().equals(unavailableColor)) {
            //set the list for the unavailable seats(reserved seats)
            List<String> finalBeginningOfSeats = beginningOfSeats;
            List<String> finalEndOfSeats = endOfSeats;
                /* Unless the seat already is reserved, when we click on a selected seat/seats,
                 the color on the seat/seats will change to selected and will keep its color
                 regardless if we move the mouse over or leave the seat/seats with the mouse.
                */
            cinemaSeats.get(seatOne).setOnMouseClicked(event -> {
                if (numberOfPeople == 1) {
                    var start = cinemaSeats.indexOf(cinemaSeats.get(seatOne));
                    // change the firstSeatNumber value
                    firstSeatNumber = start;

                    /* When we have selected(clicked on) a seat we go through all the seats and set them to their
                    original color.
                    Selected seats get the color selected which is unchangeable unless the user clicks
                    elsewhere.
                    Couches gets the selected color, wheelchair the wheelchair color, and the rest gets
                    the unselected color.
                    To go through all the seats and set their individual color once more is needed as the
                    previously selected(clicked on) seats needs to go back to their original state, which can be
                    unselected, couch, and wheelchair.
                    */
                    cinemaSeats.forEach(item -> {
                        if (item == cinemaSeats.get(start))
                            item.setFill(selectedColor);
                        else if (finalBeginningOfSeats.contains(item.getId()) ||
                                finalEndOfSeats.contains(item.getId()))
                            item.setFill(reservedColor); // do I really need this line?
                        else if (couchSeats.contains(item))
                            item.setFill(couchColor);
                        else if (wheelchairSeats.contains(item))
                            item.setFill(wheelchairColor);
                        else
                            item.setFill(unselectedColor);
                    });

                    // set the already reserved seats
                    fillUnavailableSeats(finalBeginningOfSeats, finalEndOfSeats);

                    // set the text to show the user which seat has been selected
                    chosenSeatText.setText("Seat " + cinemaSeats.get(firstSeatNumber).getId());
                }

                else if (numberOfPeople == 2
                        && !cinemaSeats.get(seatTwo).getFill().toString().equals(unavailableColor)) {
                    var start = cinemaSeats.indexOf(cinemaSeats.get(seatOne));
                    var end = cinemaSeats.indexOf(cinemaSeats.get(seatTwo));
                    // change the firstSeatNumber and the lastSeatNumbers value
                    firstSeatNumber = start;
                    lastSeatNumber = end;

                        /* When we have selected(clicked on) a seat we go through all the seats and set them to their
                        original color.
                        Selected seats get the color selected which is unchangeable unless the user clicks
                        elsewhere.
                        Couches gets the selected color, wheelchair the wheelchair color, and the rest gets
                        the unselected color.
                        To go through all the seats and set their individual color once more is needed as the
                        previously selected(clicked on) seats needs to go back to their original state, which can be
                        unselected, couch, and wheelchair.
                        */
                    cinemaSeats.forEach(item -> {
                        if (item == cinemaSeats.get(start) || item == cinemaSeats.get(end))
                            item.setFill(selectedColor);
                        else if (couchSeats.contains(item))
                            item.setFill(couchColor);
                        else if (wheelchairSeats.contains(item))
                            item.setFill(wheelchairColor);
                        else
                            item.setFill(unselectedColor);
                    });

                    // set the already reserved seats
                    fillUnavailableSeats(finalBeginningOfSeats, finalEndOfSeats);

                    // set the text to show the user which seats has been selected
                    chosenSeatText.setText(
                            "Seat " + cinemaSeats.get(firstSeatNumber).getId()
                            + " - " + cinemaSeats.get(lastSeatNumber).getId());
                }

                else if (numberOfPeople == 3 &&
                        !cinemaSeats.get(seatThree).getFill().toString().equals(unavailableColor)) {
                    var start = cinemaSeats.indexOf(cinemaSeats.get(seatOne));
                    var end = cinemaSeats.indexOf(cinemaSeats.get(seatThree));
                    // change the firstSeatNumber and the lastSeatNumbers value
                    firstSeatNumber = start;
                    lastSeatNumber = end;

                        /* When we have selected(clicked on) a seat we go through all the seats and set them to their
                        original color.
                        Selected seats get the color selected which is unchangeable unless the user clicks
                        elsewhere.
                        Couches gets the selected color, wheelchair the wheelchair color, and the rest gets
                        the unselected color.
                        To go through all the seats and set their individual color once more is needed as the
                        previously selected(clicked on) seats needs to go back to their original state, which can be
                        unselected, couch, and wheelchair.
                        */
                    cinemaSeats.forEach(item -> {
                        if (item == cinemaSeats.get(start) ||
                                item == cinemaSeats.get(start + 1) ||
                                item == cinemaSeats.get(end))
                            item.setFill(selectedColor);
                        else if (couchSeats.contains(item))
                            item.setFill(couchColor);
                        else if (wheelchairSeats.contains(item))
                            item.setFill(wheelchairColor);
                        else
                            item.setFill(unselectedColor);
                    });

                    // set the already reserved seats
                    fillUnavailableSeats(finalBeginningOfSeats, finalEndOfSeats);

                    // set the text to show the user which seats has been selected
                    chosenSeatText.setText(
                            "Seat " + cinemaSeats.get(firstSeatNumber).getId()
                            + " - " + cinemaSeats.get(lastSeatNumber).getId());
                }
            });
        }
    }

    /**
     * Colors the recommended seats with the selected color and shows the user the recommended seats
     * which is adapted to the number of people.
     *
     * @param cinemaSeats a list of all the seats.
     * @param notAvailableIfTwo a list of all the seats that will be unavailable if there are two people.
     * @param notAvailableIfThree a list of all the seats that will be unavailable if there are three people.
     * @param numberOfPeople number of tickets.
     * @param chosenSeatText the seats that will be shown to the user.
     * @param firstSeatNumber the first selected seat which is chosen by the Random method.
     * @param lastSeatNumber the last selected seat which is chosen by the Random method.
     */
    private void fillRecommendedSeats(List<FontAwesomeIconView> cinemaSeats,
                                      List<Integer> notAvailableIfTwo,
                                      List<Integer> notAvailableIfThree,
                                      int numberOfPeople,
                                      Text chosenSeatText,
                                      int firstSeatNumber,
                                      int lastSeatNumber) {
        var selected = Color.valueOf("#ffffff");

        if (numberOfPeople == 1) {
            cinemaSeats.get(firstSeatNumber).setFill(selected);

            chosenSeatText.setText("Seat " + cinemaSeats.get(firstSeatNumber).getId());
        }

        if (numberOfPeople == 2) {
            cinemaSeats.get(firstSeatNumber).setFill(selected);
            cinemaSeats.get(lastSeatNumber).setFill(selected);

            chosenSeatText.setText(
                    "Seat " + cinemaSeats.get(firstSeatNumber).getId()
                            + " - " + cinemaSeats.get(lastSeatNumber).getId());
        }

        if (numberOfPeople == 3 &&
                !notAvailableIfThree.contains(firstSeatNumber) &&
                !notAvailableIfTwo.contains(lastSeatNumber)) {
            cinemaSeats.get(firstSeatNumber).setFill(selected);
            cinemaSeats.get(lastSeatNumber).setFill(selected);
            cinemaSeats.get(lastSeatNumber +1).setFill(selected);

            chosenSeatText.setText(
                    "Seat " + cinemaSeats.get(firstSeatNumber).getId()
                            + " - " + cinemaSeats.get(lastSeatNumber +1).getId());
        }

    }

    /**
     * Goes through all the seats and if the seat is reserved it will change color to the reserved one.
     *
     * @param finalBeginningOfSeats a list containing all the first seats that the user has reserved.
     * @param finalEndOfSeats a list containing all the last seats that the user has reserved.
     */
    private void fillUnavailableSeats(List<String> finalBeginningOfSeats, List<String> finalEndOfSeats) {
        var reserved = Color.valueOf("#1e1c1c");
        try {
            for (var j = 0; j < finalBeginningOfSeats.size(); j++) {
                for (var k = 0; k < cinemaSeats.size(); k++) {
                    var startSeat = finalBeginningOfSeats.get(j);
                    var endSeat = finalEndOfSeats.get(j);
                    if (cinemaSeats.get(k).getId().equals(startSeat)) {
                        cinemaSeats.get(k).setFill(reserved);
                        while (!endSeat.equals("") && !cinemaSeats.get(k).getId().equals(endSeat))
                            cinemaSeats.get(k++).setFill(reserved);
                    }
                    if (cinemaSeats.get(k).getId().equals(endSeat))
                        cinemaSeats.get(k).setFill(reserved);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens up a new Booking Scene which is adapted for either 1, 2 or 3 people
     * depending on the current number.
     * This is necessary to be able to recommend the right amount of
     * seats to the user which is depending on number of people.
     *
     * @param mouseEvent Mouse event representing a click on the decrease button.
     * @throws Exception
     */
    public void decreaseSeats(MouseEvent mouseEvent) throws Exception {
        if (numberOfPeople == 2) {
            cinemaBookingSystem.setNumberOfTickets(1);
            cinemaBookingSystem.setCostOfTickets(1);
            SceneCreator.launchScene("/scenes/BookingScene.fxml");
        }

        if (numberOfPeople == 3) {
            cinemaBookingSystem.setNumberOfTickets(2);
            cinemaBookingSystem.setCostOfTickets(2);
            SceneCreator.launchScene("/scenes/BookingScene.fxml");
        }
    }

    /**
     * Opens up a new Booking Scene which is adapted for either 1, 2 or 3 people
     * depending on the current number.
     * This is necessary to be able to recommend the right amount of
     * seats to the user which is depending on number of people.
     *
     * @param mouseEvent Mouse event representing a click on the increase button
     * @throws Exception
     */
    public void increaseSeats(MouseEvent mouseEvent) throws Exception {
        if (numberOfPeople == 1) {
            cinemaBookingSystem.setNumberOfTickets(2);
            cinemaBookingSystem.setCostOfTickets(2);
            SceneCreator.launchScene("/scenes/BookingScene.fxml");
        }

        if (numberOfPeople == 2) {
            cinemaBookingSystem.setNumberOfTickets(3);
            cinemaBookingSystem.setCostOfTickets(3);
            SceneCreator.launchScene("/scenes/BookingScene.fxml");
        }
    }

    /**
     * If the user does not have a reservation the user will be able to reserve seats which will be put in
     * into the database that holds the reserved seats.
     *
     * @param mouseEvent Mouse event representing a click on the Reserve button
     * @throws Exception
     */
    public void reserve(MouseEvent mouseEvent) throws Exception {
        /* If the user already has a reservation in the database, and if so alerts the user.
        Hence making it impossible for a user to have two reservations at the same time to ensure
        the user don't miss-use the reservation system to get seats around him/her to later cancel the
        reservation to get extra space around him/her.
        */
        if (cinemaDatabase.hasReservation()) {
            var hasReservation = new Alert(Alert.AlertType.NONE, "You already have a reservation!", ButtonType.OK);
            alertStyle(hasReservation);

            hasReservation.showAndWait();

            if (hasReservation.getResult() == ButtonType.OK)
                hasReservation.close();

            return;
        }

        alertUserOfBookingSpecifications();
    }

    /**
     * If the user does not have a reservation then we show the user information regarding the reservation
     * such as the chosen movie, time of screening, number of tickets, and cost of tickets.
     *
     * @throws Exception
     */
    private void alertUserOfBookingSpecifications() throws Exception {
        var alert = new Alert(Alert.AlertType.NONE, "", ButtonType.YES, ButtonType.CANCEL);
        // sets the style of the alertBox
        alertStyle(alert);
        alert.setContentText("Do you want to proceed with the following reservation?\n\n" + "Movie: " + cinemaBookingSystem.getMovie() + "\n" +
                "Date: " + cinemaBookingSystem.getDate() + ", " + cinemaBookingSystem.getTime() + "\n" +
                "Seats: " + cinemaSeats.get(firstSeatNumber).getId() + " - " + cinemaSeats.get(lastSeatNumber).getId() + "\n" +
                "Tickets: " + numberOfPeople + "\n" +
                "Cost of tickets: " + cinemaBookingSystem.getCostOfTickets() + "$");

        alert.showAndWait();

        // the user has gets the option to not go through with the reservation
        if (alert.getResult() == ButtonType.CANCEL)
            alert.close();
            // the reservation is put into the database
        else {
            if (numberOfPeople == 1)
                cinemaDatabase.reserveSeats(
                        cinemaBookingSystem.getMovie(),
                        cinemaBookingSystem.getUser(),
                        cinemaSeats.get(firstSeatNumber).getId(),
                        "", cinemaBookingSystem.getYear());
            else if (numberOfPeople > 1)
                cinemaDatabase.reserveSeats(
                        cinemaBookingSystem.getMovie(),
                        cinemaBookingSystem.getUser(),
                        cinemaSeats.get(firstSeatNumber).getId(),
                        cinemaSeats.get(lastSeatNumber).getId(), cinemaBookingSystem.getYear());

            var completion = new Alert(Alert.AlertType.NONE, "Your tickets were reserved successfully!", ButtonType.OK);

            // sets the style of the alert box
            alertStyle(completion);

            completion.showAndWait();

            if (alert.getResult() == ButtonType.OK)
                completion.close();

            // takes us to the Reservation Scene to show us our reservation
            SceneCreator.launchScene("/scenes/ReservationsScene.fxml");
        }
    }

    /**
     * Sets a determined style for the alert window.
     *
     * @param alert takes and Alert object.
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
     * Redirects the user to the Home Scene
     *
     * @param mouseEvent Mouse event representing a click on the logo button.
     * @throws Exception
     */
    public void openHome(MouseEvent mouseEvent) throws Exception {
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
     * Redirects the user to the Login Scene.
     *
     * @param mouseEvent Mouse event representing a click on the logout button.
     * @throws Exception
     */
    public void logout(MouseEvent mouseEvent) throws Exception {
        SceneCreator.launchScene("/scenes/LoginScene.fxml");
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