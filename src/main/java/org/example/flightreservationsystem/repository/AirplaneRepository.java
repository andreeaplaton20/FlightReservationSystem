package org.example.flightreservationsystem.repository;

import org.example.flightreservationsystem.model.Airplane;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirplaneRepository extends JpaRepository<Airplane, Long> {
}
