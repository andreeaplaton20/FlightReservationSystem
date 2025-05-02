package org.example.flightreservationsystem.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AddFlightRequest {
    private int flightNumber;
    private String airlineName;
    private String airlineCode;
    private String airlineCountry;
    private String departureAirportName;
    private String departureAirportCode;
    private String departureCity;
    private String departureCountry;
    private String arrivalAirportName;
    private String arrivalAirportCode;
    private String arrivalCity;
    private String arrivalCountry;
    private String airplaneModel;
    private int airplaneCapacity;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int availableSeats;
    private BigDecimal price;
}
