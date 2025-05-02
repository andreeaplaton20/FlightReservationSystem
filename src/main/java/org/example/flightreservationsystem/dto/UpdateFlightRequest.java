package org.example.flightreservationsystem.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateFlightRequest {
    private Long id;

    private String flightNumber;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int availableSeats;
    private BigDecimal price;

    private String airlineName;
    private String airlineCode;
    private String airlineCountry;

    private String airplaneModel;
    private int airplaneCapacity;

    private String departureAirportName;
    private String departureAirportCode;
    private String departureCity;
    private String departureCountry;

    private String arrivalAirportName;
    private String arrivalAirportCode;
    private String arrivalCity;
    private String arrivalCountry;
}
