package org.example.flightreservationsystem.controller;

import jakarta.servlet.http.HttpSession;
import lombok.*;
import org.example.flightreservationsystem.dto.AddFlightRequest;
import org.example.flightreservationsystem.dto.BuyTicketRequest;
import org.example.flightreservationsystem.dto.UpdateFlightRequest;
import org.example.flightreservationsystem.model.Flight;
import org.example.flightreservationsystem.service.FlightService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/flights")
@RequiredArgsConstructor

public class FlightController {
    private final FlightService flightService;

    @GetMapping("/page")
    public String showFlightsPage(Model model, HttpSession session) {
        List<Flight> flights = flightService.flightList();
        model.addAttribute("flights", flights);
        String username = (String) session.getAttribute("username");
        if (username != null) {
            model.addAttribute("username", username);
        }
        return "flights";
    }

    @GetMapping("/admin")
    public String showAdminFlightsPage(Model model, HttpSession session) {
        List<Flight> flights = flightService.flightList();
        model.addAttribute("flights", flights);

        String username = (String) session.getAttribute("username");
        if (username != null) {
            model.addAttribute("username", username);
        }
        return "admin-flights";
    }

    @GetMapping
    public List<Flight> getAllFlights() {
        return flightService.flightList();
    }

    @PostMapping
    public Flight createFlight(@RequestBody Flight flight) {
        return flightService.createFlight(flight);
    }

    @PostMapping("/add")
    public String addFlight(@ModelAttribute AddFlightRequest request, Model model) {
        flightService.addFlight(request);
        model.addAttribute("message", "Flight added successfully!");
        return "redirect:/api/flights/admin";
    }

    @PostMapping("/buy-ticket")
    public String buyTicket(@ModelAttribute BuyTicketRequest request, Model model) {
        boolean success = flightService.buyTicket(request.getFlightId(), request.getTicketCount());

        if (success) {
            return "ticket-success";
        } else {
            model.addAttribute("errorMessage", "Failed to buy ticket. Please check flight availability.");
            return "ticket-error";
        }
    }

    @PostMapping("/delete")
    public String deleteFlight(@RequestParam Long flightId) {
        flightService.deleteFlightById(flightId);
        return "redirect:/api/flights/admin";
    }

    @GetMapping("/edit/{flightId}")
    public String editFlightForm(@PathVariable Long flightId, Model model) {
        Flight flight = flightService.getFlightById(flightId);
        model.addAttribute("flight", flight);
        return "edit-flight";
    }

    @PostMapping("/update")
    public String updateFlight(@ModelAttribute UpdateFlightRequest request) {
        flightService.updateFlight(request);
        return "redirect:/api/flights/admin";
    }
}
