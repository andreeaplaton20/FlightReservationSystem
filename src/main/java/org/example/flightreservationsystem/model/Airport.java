package org.example.flightreservationsystem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor

public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(length = 3, unique = true)
    private String iataCode;

    private String city;
    private String country;

    public Airport(String name, String iataCode, String city, String country) {
        this.name = name;
        this.iataCode = iataCode;
        this.city = city;
        this.country = country;
    }
}
