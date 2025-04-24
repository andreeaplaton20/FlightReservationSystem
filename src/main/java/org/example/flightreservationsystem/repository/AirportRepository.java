package org.example.flightreservationsystem.repository;

import org.example.flightreservationsystem.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Long> {
}
