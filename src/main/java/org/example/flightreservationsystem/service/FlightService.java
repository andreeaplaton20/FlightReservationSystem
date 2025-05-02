package org.example.flightreservationsystem.service;

import lombok.*;
import org.example.flightreservationsystem.dto.AddFlightRequest;
import org.example.flightreservationsystem.dto.UpdateFlightRequest;
import org.example.flightreservationsystem.model.*;
import org.example.flightreservationsystem.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final AirplaneRepository airplaneRepository;

    public List<Flight> flightList() {
        return flightRepository.findAll();
    }

    public Flight createFlight(Flight flight) {
        return flightRepository.save(flight);
    }

    public void addFlight(AddFlightRequest request) {
        Optional<Airline> existingAirline = airlineRepository.findByIataCode(request.getAirlineCode());
        Airline airline;
        if (existingAirline.isPresent()) {
            airline = existingAirline.get();
        } else {
            airline = new Airline(request.getAirlineName(), request.getAirlineCode(), request.getAirlineCountry());
            airline = airlineRepository.save(airline);
        }

        Optional<Airport> existingDepartureAirport = airportRepository.findByIataCode(request.getDepartureAirportCode());
        Airport departureAirport;
        if (existingDepartureAirport.isPresent()) {
            departureAirport = existingDepartureAirport.get();
        } else {
            departureAirport = new Airport(request.getDepartureAirportName(),
                                            request.getDepartureAirportCode(),
                                            request.getDepartureCity(),
                                            request.getDepartureCountry());
            departureAirport = airportRepository.save(departureAirport);
        }

        Optional<Airport> existingArrivalAirport = airportRepository.findByIataCode(request.getArrivalAirportCode());
        Airport arrivalAirport;
        if (existingArrivalAirport.isPresent()) {
            arrivalAirport = existingArrivalAirport.get();
        } else {
            arrivalAirport = new Airport(
                    request.getArrivalAirportName(),
                    request.getArrivalAirportCode(),
                    request.getArrivalCity(),
                    request.getArrivalCountry()
            );
            arrivalAirport = airportRepository.save(arrivalAirport);
        }

        Airplane airplane = new Airplane(request.getAirplaneModel(), request.getAirplaneCapacity(), airline);
        airplane = airplaneRepository.save(airplane);

        Flight flight = new Flight(request.getFlightNumber(),
                                    departureAirport,
                                    arrivalAirport,
                                    request.getDepartureTime(),
                                    request.getArrivalTime(),
                                    request.getAvailableSeats(),
                                    request.getPrice());
        flight.setAirplane(airplane);
        flightRepository.save(flight);
    }

    public boolean buyTicket(Long flightId, int ticketCount) {
        return flightRepository.findById(flightId).map(flight -> {
            if (flight.getAvailableSeats() >= ticketCount) {
                flight.setAvailableSeats(flight.getAvailableSeats() - ticketCount);
                flightRepository.save(flight);
                return true;
            } else {
                return false;
            }
        }).orElse(false);
    }

    public void deleteFlightById(Long flightId) {
        flightRepository.deleteById(flightId);
    }

    public Flight getFlightById(Long flightId) {
        return flightRepository.findById(flightId).orElseThrow(()-> new RuntimeException("Flight not found"));
    }

    public void updateFlight(UpdateFlightRequest request) {
        Flight flight = flightRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        Airline airline = flight.getAirplane().getAirline();
        airline.setName(request.getAirlineName());
        airline.setIataCode(request.getAirlineCode());
        airline.setCountry(request.getAirlineCountry());
        airlineRepository.save(airline);

        Airplane airplane = flight.getAirplane();
        airplane.setModel(request.getAirplaneModel());
        airplane.setTotalSeats(request.getAirplaneCapacity());
        airplane.setAirline(airline);
        airplaneRepository.save(airplane);

        Airport departureAirport = flight.getDepartureAirport();
        departureAirport.setName(request.getDepartureAirportName());
        departureAirport.setIataCode(request.getDepartureAirportCode());
        departureAirport.setCity(request.getDepartureCity());
        departureAirport.setCountry(request.getDepartureCountry());
        airportRepository.save(departureAirport);

        Airport arrivalAirport = flight.getArrivalAirport();
        arrivalAirport.setName(request.getArrivalAirportName());
        arrivalAirport.setIataCode(request.getArrivalAirportCode());
        arrivalAirport.setCity(request.getArrivalCity());
        arrivalAirport.setCountry(request.getArrivalCountry());
        airportRepository.save(arrivalAirport);

        flight.setFlightNumber(Integer.parseInt(request.getFlightNumber()));
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setAvailableSeats(request.getAvailableSeats());
        flight.setPrice(request.getPrice());
        flight.setAirplane(airplane);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);

        flightRepository.save(flight);
    }

}
