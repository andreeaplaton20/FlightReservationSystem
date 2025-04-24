package org.example.flightreservationsystem;

import lombok.*;
import org.example.flightreservationsystem.model.Flight;

public class Reservation {
    @Getter
    private int id;
    @Getter
    private String passengerName;
    @Getter
    private String email;
    @Getter
    private Flight flight;
    @Getter
    private int seatNumber;
    @Getter
    private String reservationDate;
}
