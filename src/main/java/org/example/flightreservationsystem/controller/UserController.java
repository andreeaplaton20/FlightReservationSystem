package org.example.flightreservationsystem.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.flightreservationsystem.service.UserService;
import org.example.flightreservationsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/custom-login")
    public String loginForm() {
        return "custom-login";
    }

    @PostMapping("/custom-login")
    public String loginSubmit(@RequestParam String username,
                              @RequestParam String password,
                              Model model,
                              HttpSession session) {
        if (userService.authenticate(username, password)) {
            session.setAttribute("username", username);
            userRepository.findByUsername(username).ifPresent(user -> {
                session.setAttribute("email", user.getEmail());
            });
            if (!userService.isAdmin(username)) {
                return "redirect:/api/flights/page";
            } else {
                return "redirect:/api/flights/admin";
            }
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "custom-login";
        }
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam String username,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 Model model) {
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        userService.register(username, email, password, "user");
        model.addAttribute("success", "Registration successful! You can now log in.");
        return "redirect:/api/users/custom-login";
    }
}
