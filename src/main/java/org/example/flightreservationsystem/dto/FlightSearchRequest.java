package org.example.flightreservationsystem.dto;

public class FlightSearchRequest {
    public Long id;
    public String destinationCity;
    public String destinationAirport;
    public String departureTime;

    public FlightSearchRequest(Long id, String city, String airport, String departureTime) {
        this.id = id;
        this.destinationCity = city;
        this.destinationAirport = airport;
        this.departureTime = departureTime;
    }
}