package org.example.flightreservationsystem.repository;

import org.example.flightreservationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
//    Optional<User> findByEmail(String email);
}
