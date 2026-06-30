package com.interswitch.verveguardfraudservice.controller;

import com.interswitch.verveguardfraudservice.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        if (request.username().equals("admin") && request.password().equals("password")) {
            return ResponseEntity.ok(new AuthResponse(jwtService.generateToken(request.username())));
        }

        throw new IllegalArgumentException("Invalid username or password");
    }
}

record AuthRequest(String username, String password) {}
record AuthResponse(String token) {}