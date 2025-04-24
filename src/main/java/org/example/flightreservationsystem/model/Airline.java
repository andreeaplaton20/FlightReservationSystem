package org.example.flightreservationsystem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor

public class Airline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String name;

    @Column(length = 3, unique = true)
    public String iataCode;

    public String country;

    public Airline(String name, String iataCode, String country) {
        this.name = name;
        this.iataCode = iataCode;
        this.country = country;
    }
}
