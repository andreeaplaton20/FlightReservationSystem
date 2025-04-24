package org.example.flightreservationsystem.repository;

import org.example.flightreservationsystem.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {
}
