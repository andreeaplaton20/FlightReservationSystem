package org.example.flightreservationsystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;
    @Column(name = "reserved")
    private boolean reserved;
    @Column(name = "reserved_until")
    private LocalDateTime reservedUntil;

    @ManyToOne
    private Flight flight;
}
