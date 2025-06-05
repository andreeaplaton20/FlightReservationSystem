package org.example.flightreservationsystem.repository;

import org.example.flightreservationsystem.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    Optional<Flight> findByFlightNumber(Integer flightNumber);
}
