package org.example.flightreservationsystem.repository;

import org.example.flightreservationsystem.model.Flight;
import org.example.flightreservationsystem.model.Seat;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByFlightId(Long flightId);
    List<Seat> findByFlightAndReservedFalse(Flight flight);

    List<Seat> findByFlightAndSeatNumberIn(Flight flight, List<String> seatNumbers);

    @Query("SELECT s FROM Seat s WHERE s.flight = :flight AND s.reserved = false")
    List<Seat> findTopNByFlightAndReservedFalse(@Param("flight") Flight flight, Pageable pageable);

    default List<Seat> findTopNByFlightAndReservedFalse(Flight flight, int n) {
        return findTopNByFlightAndReservedFalse(flight, PageRequest.of(0, n));
    }
}
