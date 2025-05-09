package org.example.flightreservationsystem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;
    private boolean reserved;

    @ManyToOne
    private Flight flight;
}
