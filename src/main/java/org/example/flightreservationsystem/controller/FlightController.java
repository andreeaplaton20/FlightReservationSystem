package org.example.flightreservationsystem.controller;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.*;
import org.example.flightreservationsystem.dto.AddFlightRequest;
import org.example.flightreservationsystem.dto.BuyTicketRequest;
import org.example.flightreservationsystem.dto.UpdateFlightRequest;
import org.example.flightreservationsystem.model.Flight;
import org.example.flightreservationsystem.model.Seat;
import org.example.flightreservationsystem.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.nio.charset.StandardCharsets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String buyTicket(@ModelAttribute BuyTicketRequest request, Model model, HttpSession session) {
        boolean manualSelection = request.getSelectedSeatNumbers() != null && !request.getSelectedSeatNumbers().isEmpty();

        boolean success = flightService.buyTicket(
                request.getFlightId(),
                request.getTicketCount(),
                request.getSelectedSeatNumbers()
        );

        if (success) {
            Flight flight = flightService.getFlightById(request.getFlightId());
            BigDecimal ticketPrice = flight.getPrice();
            BigDecimal extraCost = manualSelection
                    ? new BigDecimal("25.00").multiply(BigDecimal.valueOf(request.getTicketCount()))
                    : BigDecimal.ZERO;

            BigDecimal totalPrice = ticketPrice.multiply(BigDecimal.valueOf(request.getTicketCount())).add(extraCost);

            session.setAttribute("departure", flight.getDepartureAirport().getCity());
            session.setAttribute("departureTime", flight.getDepartureTime().toString());
            session.setAttribute("destination", flight.getArrivalAirport().getCity());
            session.setAttribute("arrivalTime", flight.getArrivalTime().toString());

            model.addAttribute("flight", flight);
            model.addAttribute("assignedSeat", manualSelection ? request.getSelectedSeatNumbers().toString() : "Auto-assigned at check-in");
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("extraCost", extraCost);
            model.addAttribute("manualSelection", manualSelection);

            return "ticket-success";
        } else {
            model.addAttribute("errorMessage", "Failed to buy ticket. Please check flight availability.");
            return "ticket-error";
        }
    }

    @GetMapping("/buy-ticket-auto")
    public String buyTicketAuto(@RequestParam Long flightId, @RequestParam int ticketCount, Model model, HttpSession session) {
        boolean success = flightService.buyTicket(flightId, ticketCount, null);

        if (success) {
            Flight flight = flightService.getFlightById(flightId);
            String assignedSeat = flightService.getAssignedSeats(flightId, ticketCount);

            session.setAttribute("departure", flight.getDepartureAirport().getCity());
            session.setAttribute("departureTime", flight.getDepartureTime().toString());
            session.setAttribute("destination", flight.getArrivalAirport().getCity());
            session.setAttribute("arrivalTime", flight.getArrivalTime().toString());

            model.addAttribute("flight", flight);
            model.addAttribute("assignedSeat", assignedSeat);
            model.addAttribute("ticketCount", ticketCount);
            model.addAttribute("totalPrice", flight.getPrice().multiply(BigDecimal.valueOf(ticketCount)));

            return "ticket-success";
        } else {
            model.addAttribute("errorMessage", "Failed to buy ticket. Please check flight availability.");
            return "ticket-error";
        }
    }


    @GetMapping("/select-seats")
    public String selectSeats(@RequestParam Long flightId, @RequestParam int count, Model model) {
        Flight flight = flightService.getFlightById(flightId);
        List<Seat> allSeats = flightService.getAllSeatsForFlight(flightId);

        model.addAttribute("flight", flight);
        model.addAttribute("seats", allSeats);
        model.addAttribute("ticketCount", count);
        return "select-seats";
    }

    @PostMapping("/simulate-payment")
    @ResponseBody
    public Map<String, Object> simulatePayment(
            @RequestParam String cardNumber,
            @RequestParam String cardholderName,
            @RequestParam String cardholderLastName,
            @RequestParam String expiry,
            @RequestParam String cvv,
            @RequestParam BigDecimal totalPrice,
            @RequestParam String flightNumber,
            HttpSession session
    ) {
        String email = (String) session.getAttribute("email");
        String departure = (String) session.getAttribute("departure");
        String departureTime = (String) session.getAttribute("departureTime");
        String destination = (String) session.getAttribute("destination");
        String arrivalTime = (String) session.getAttribute("arrivalTime");
        Map<String, Object> response = new HashMap<>();
        response.put("flightNumber", flightNumber);
        response.put("cardholder", cardholderName + " " + cardholderLastName);
        response.put("totalPrice", totalPrice);

        if (email == null) {
            response.put("paymentSuccess", false);
            response.put("error", "User email not found in session.");
            return response;
        }
        boolean paymentSuccess = simulateCardPayment(cardNumber, expiry, cvv);
        response.put("paymentSuccess", paymentSuccess);

        if (paymentSuccess) {
            try {
                String html = loadHtmlTemplate("ticket.html", Map.of(
                        "flightNumber", flightNumber,
                        "firstName", cardholderName,
                        "lastName", cardholderLastName,
                        "departure", departure,
                        "departureTime", departureTime,
                        "arrivalTime", arrivalTime,
                        "destination", destination,
                        "totalPrice", totalPrice.toPlainString()
                ));
                byte[] pdf = generateTicketPdf(html);

                sendTicketEmail(email, pdf, "ticket.pdf");
            } catch (Exception e) {
                e.printStackTrace();
                response.put("paymentSuccess", false);
                response.put("error", "Failed to send email.");
            }
        }

        return response;
    }

    private boolean simulateCardPayment(String cardNumber, String expiry, String cvv) {
        return (cardNumber.startsWith("4") || cardNumber.startsWith("5")) &&
                expiry.matches("\\d{2}/\\d{2}") && cvv.matches("\\d{3}");
    }

    @Autowired
    private JavaMailSender mailSender;

    public void sendTicketEmail(String to, byte[] pdfData, String fileName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Your Flight Ticket");
        helper.setText("Thank you for your payment. Please find your ticket attached.");

        ByteArrayResource attachment = new ByteArrayResource(pdfData);
        helper.addAttachment(fileName, attachment);

        mailSender.send(message);
    }

    private byte[] generateTicketPdf(String htmlContent) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        return outputStream.toByteArray();
    }

    private String loadHtmlTemplate(String templateName, Map<String, String> values) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + templateName);
        String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        for (Map.Entry<String, String> entry : values.entrySet()) {
            html = html.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return html;
    }

    @GetMapping("/download-ticket")
    public ResponseEntity<ByteArrayResource> downloadTicket(
            @RequestParam String flightNumber,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String departure,
            @RequestParam String destination,
            @RequestParam BigDecimal totalPrice) {

        try {
            String html = loadHtmlTemplate("ticket-template.html", Map.of(
                    "flightNumber", flightNumber,
                    "firstName", firstName,
                    "lastName", lastName,
                    "departure", departure,
                    "destination", destination,
                    "totalPrice", totalPrice.toPlainString()
            ));
            byte[] pdf = generateTicketPdf(html);
            ByteArrayResource resource = new ByteArrayResource(pdf);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=ticket.pdf")
                    .contentLength(pdf.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
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
