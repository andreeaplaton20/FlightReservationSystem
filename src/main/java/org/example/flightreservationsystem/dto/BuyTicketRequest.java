package org.example.flightreservationsystem.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class BuyTicketRequest {
    private Long flightId;
    private int ticketCount;
    @Setter
    @Getter
    private List<String> selectedSeatNumbers;
}
