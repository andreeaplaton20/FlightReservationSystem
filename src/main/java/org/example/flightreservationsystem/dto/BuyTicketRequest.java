package org.example.flightreservationsystem.dto;

import lombok.Data;

@Data
public class BuyTicketRequest {
    private Long flightId;
    private int ticketCount;
}
