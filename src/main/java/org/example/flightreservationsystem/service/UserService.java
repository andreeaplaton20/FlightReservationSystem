package org.example.flightreservationsystem.service;

import lombok.Setter;
import org.example.flightreservationsystem.User;
import org.example.flightreservationsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.flightreservationsystem.util.PasswordEncoderUtil;

@Service
public class UserService {
    @Autowired
    @Setter
    private UserRepository userRepository;

    public User register(String username, String email, String password, String role) {
        String encodedPassword = PasswordEncoderUtil.encode(password);
        User user = User.builder()
                .username(username)
                .email(email)
                .password(encodedPassword)
                .role(role)
                .build();
        return userRepository.save(user);
    }

    public boolean authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> PasswordEncoderUtil.matches(password, user.getPassword()))
                .orElse(false);
    }
}
