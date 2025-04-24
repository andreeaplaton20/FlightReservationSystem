package org.example.flightreservationsystem.model;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor

public class Airplane {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String model;
    private int totalSeats;

    @ManyToOne
    @JoinColumn(name = "airline_id")
    private Airline airline;

    public Airplane(String model, int totalSeats, Airline airline) {
        this.model = model;
        this.totalSeats = totalSeats;
        this.airline = airline;
    }
}
