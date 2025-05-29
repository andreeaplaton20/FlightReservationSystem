package org.example.flightreservationsystem.service;

import lombok.*;
import org.example.flightreservationsystem.dto.AddFlightRequest;
import org.example.flightreservationsystem.dto.UpdateFlightRequest;
import org.example.flightreservationsystem.model.*;
import org.example.flightreservationsystem.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final AirplaneRepository airplaneRepository;
    private final SeatRepository seatRepository;

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

        int totalSeats = airplane.getTotalSeats();
        int rows = (int) Math.ceil((double) totalSeats / 6);
        generateSeatsForFlight(flight, rows);
    }

    public boolean buyTicket(Long flightId, int ticketCount, List<String> selectedSeatNumbers) {
        Optional<Flight> optionalFlight = flightRepository.findById(flightId);
        if (optionalFlight.isEmpty()) return false;
        Flight flight = optionalFlight.get();
        if (flight.getAvailableSeats() < ticketCount) {
            return false;
        }

        List<Seat> seatsToReserve;

        if (selectedSeatNumbers != null && !selectedSeatNumbers.isEmpty()) {
            seatsToReserve = seatRepository.findByFlightAndSeatNumberIn(flight, selectedSeatNumbers);
            if (seatsToReserve.size() != ticketCount) return false;
        } else {
            seatsToReserve = seatRepository.findTopNByFlightAndReservedFalse(flight, ticketCount);
        }

        for (Seat seat : seatsToReserve) {
            seat.setReserved(true);
        }
        seatRepository.saveAll(seatsToReserve);

        flight.setAvailableSeats(flight.getAvailableSeats() - ticketCount);
        flightRepository.save(flight);

        return true;
    }

    public List<Seat> getAvailableSeatsForFlight(Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        return seatRepository.findByFlightAndReservedFalse(flight);
    }

    public String getAssignedSeats(Long flightId, int ticketCount) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        List<Seat> availableSeats = seatRepository.findByFlightAndReservedFalse(flight);
        if (availableSeats.size() < ticketCount) {
            return null;
        }
        StringBuilder assignedSeats = new StringBuilder();
        for (int i = 0; i < ticketCount; i++) {
            assignedSeats.append(availableSeats.get(i).getSeatNumber()).append(" ");
        }
        return assignedSeats.toString().trim();
    }

    public List<Seat> getAllSeatsForFlight(Long flightId) {
        return seatRepository.findByFlightId(flightId);
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

        int totalSeats = airplane.getTotalSeats();
        int rows = (int) Math.ceil((double) totalSeats / 6);
        generateSeatsForFlight(flight, rows);
    }

    public void generateSeatsForFlight(Flight flight, int rows) {
        String[] seatLetters = {"A", "B", "C", "D", "E", "F"};
        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= rows; row++) {
            for (String letter : seatLetters) {
                Seat seat = new Seat();
                seat.setSeatNumber(row + letter);
                seat.setReserved(false);
                seat.setFlight(flight);
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
    }

    public boolean reserveSeatsTemporarily(Long flightId, List<String> seatNumbers) {
        Flight flight = flightRepository.findById(flightId).orElse(null);
        if (flight == null) return false;

        List<Seat> seats = seatRepository.findByFlightAndSeatNumberIn(flight, seatNumbers).stream()
                .filter(seat -> !seat.isReserved() && (seat.getReservedUntil() == null || seat.getReservedUntil().isBefore(LocalDateTime.now())))
                .toList();

        if (seats.size() != seatNumbers.size()) return false;

        for (Seat seat : seats) {
            seat.setReservedUntil(LocalDateTime.now().plusMinutes(15));
        }

        seatRepository.saveAll(seats);
        return true;
    }

    public void finalizeReservation(Long flightId, List<String> seatNumbers) {
        Flight flight = flightRepository.findById(flightId).orElse(null);
        if (flight == null) return;

        List<Seat> seats = seatRepository.findByFlightAndSeatNumberIn(flight, seatNumbers);
        for (Seat seat : seats) {
            seat.setReserved(true);
            seat.setReservedUntil(null);
        }

        seatRepository.saveAll(seats);
    }

    @Scheduled(fixedRate = 60000)
    public void releaseExpiredSeats() {
        List<Seat> expiredSeats = seatRepository.findAll().stream()
                .filter(seat -> !seat.isReserved() && seat.getReservedUntil() != null && seat.getReservedUntil().isBefore(LocalDateTime.now()))
                .toList();

        for (Seat seat : expiredSeats) {
            seat.setReservedUntil(null);
        }

        seatRepository.saveAll(expiredSeats);
    }

    public List<Seat> autoAssignAndReserveSeats(Long flightId, int ticketCount) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        List<Seat> availableSeats = seatRepository.findByFlightAndReservedFalse(flight);

        if (availableSeats.size() < ticketCount) {
            throw new RuntimeException("Not enough available seats for this flight.");
        }

        Collections.shuffle(availableSeats);

        List<Seat> assignedSeats = availableSeats.stream()
                .limit(ticketCount)
                .toList();

        for (Seat seat : assignedSeats) {
            seat.setReserved(true);
        }
        seatRepository.saveAll(assignedSeats);

        flight.setAvailableSeats(flight.getAvailableSeats() - ticketCount);
        flightRepository.save(flight);

        return assignedSeats;
    }
}
