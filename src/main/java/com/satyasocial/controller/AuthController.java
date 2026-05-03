package com.satyasocial.controller;

import com.satyasocial.dto.LoginRequest;
import com.satyasocial.dto.RegisterRequest;
import com.satyasocial.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            String token = authService.register(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "token", token,
                "message", "Registration successful. Welcome to SatyaSocial!"
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409)
                .body(Map.of(
                    "success", false,
                    "error", e.getMessage()
                ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "success", false,
                    "error", e.getMessage()
                ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "token", token,
                "message", "Login successful"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                .body(Map.of(
                    "success", false,
                    "error", "Invalid username or password"
                ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
            "status", "SatyaSocial is running!",
            "verified", true
        ));
    }

    // TEST ONLY — Remove before production
    @PostMapping("/register/test")
    public ResponseEntity<?> registerTest(@RequestBody RegisterRequest request) {
        try {
            String token = authService.registerTest(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "token", token,
                    "message", "Test registration successful"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}