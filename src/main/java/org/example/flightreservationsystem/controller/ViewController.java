package org.example.flightreservationsystem.controller;

import org.springframework.ui.Model;
import org.example.flightreservationsystem.service.FlightService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    private final FlightService flightService;

    public ViewController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "Let's fly together!");
        return "FlightReservationSystem";
    }

    @GetMapping("/custom-login")
    public String login() {
        return "custom-login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/flights")
    public String flights(Model model) {
        model.addAttribute("flights", flightService.flightList());
        return "flights";
    }
}
