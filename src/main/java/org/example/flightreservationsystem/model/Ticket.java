package org.example.flightreservationsystem.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String passengerFirstName;
    private String passengerLastName;
    private String email;

    @ManyToOne
    private Flight flight;

    private String seatNumbers;
    private int ticketsCount;

    private BigDecimal totalPrice;

    private LocalDateTime purchaseTime;
}
