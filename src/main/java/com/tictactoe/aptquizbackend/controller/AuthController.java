package com.tictactoe.aptquizbackend.controller;

import com.tictactoe.aptquizbackend.dto.UserResponse;
import com.tictactoe.aptquizbackend.entity.User;
import com.tictactoe.aptquizbackend.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public UserResponse login(@RequestParam String email,
                              @RequestParam String password) {

        return authService.login(email, password);
    }
}