package org.example.flightreservationsystem.repository;

import org.example.flightreservationsystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByEmailOrderByPurchaseTimeDesc(String email);
}