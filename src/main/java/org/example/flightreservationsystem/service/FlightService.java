package org.example.flightreservationsystem.service;

import lombok.*;
import org.example.flightreservationsystem.model.*;
import org.example.flightreservationsystem.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;

    public List<Flight> flightList() {
        return flightRepository.findAll();
    }

    public Flight createFlight(Flight flight) {
        return flightRepository.save(flight);
    }
}
