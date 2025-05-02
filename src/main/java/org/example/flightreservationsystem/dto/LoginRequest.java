package org.example.flightreservationsystem.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
}
