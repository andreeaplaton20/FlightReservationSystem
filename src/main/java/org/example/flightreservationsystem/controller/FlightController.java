package org.example.flightreservationsystem.controller;

import lombok.*;
import org.example.flightreservationsystem.model.Flight;
import org.example.flightreservationsystem.service.FlightService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor

public class FlightController {
    private final FlightService flightService;

    @GetMapping
    public List<Flight> getAllFlights() {
        return flightService.flightList();
    }

    @PostMapping
    public Flight createFlight(@RequestBody Flight flight) {
        return flightService.createFlight(flight);
    }
}
