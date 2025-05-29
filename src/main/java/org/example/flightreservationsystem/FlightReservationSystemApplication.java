package org.example.flightreservationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FlightReservationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightReservationSystemApplication.class, args);
    }
}

