package com.satyasocial.service;

import com.satyasocial.dto.LoginRequest;
import com.satyasocial.dto.RegisterRequest;
import com.satyasocial.model.User;
import com.satyasocial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PanVerificationService panVerificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {

        // Step 1 — Verify PAN with Setu
        // Gets real name from government database
        String fullName = panVerificationService
                .verifyAndGetName(request.getPan());

        // Step 2 — Hash PAN before storing
        // Original PAN never stored anywhere
        String panHash = hashPan(request.getPan());

        // Step 3 — One person one account check
        if (userRepository.existsByPanHash(panHash)) {
            throw new IllegalStateException(
                "An account already exists with this PAN number"
            );
        }

        // Step 4 — Username availability check
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException(
                "Username already taken. Please choose another."
            );
        }

        // Step 5 — Create verified user
        // Name comes from government database — cannot be faked
        User user = User.builder()
            .panHash(panHash)
            .fullName(fullName)
            .username(request.getUsername())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .state(request.getState())
            .ageBracket(request.getAgeBracket())
            .gender(request.getGender())
            .isPanVerified(true)
            .isActive(true)
            .build();

        userRepository.save(user);

        log.info("New verified user registered: {} from {}",
            request.getUsername(), request.getState());

        // Step 6 — Return JWT token
        return jwtService.generateToken(user);
    }

    public String login(LoginRequest request) {

        // Find user by username
        User user = userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() ->
                new IllegalArgumentException("Invalid username or password")
            );

        // Check account is active
        if (!user.getIsActive()) {
            throw new IllegalStateException("Account is deactivated");
        }

        // Verify password
        if (!passwordEncoder.matches(
                request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        log.info("User logged in: {}", request.getUsername());

        return jwtService.generateToken(user);
    }

    private String hashPan(String pan) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                pan.toUpperCase().getBytes(StandardCharsets.UTF_8)
            );
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }
    // TEST ONLY — Remove before production
    public String registerTest(RegisterRequest request) {

        String panHash = hashPan(request.getPan());

        if (userRepository.existsByPanHash(panHash)) {
            throw new IllegalStateException(
                    "An account already exists with this PAN number"
            );
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("Username already taken");
        }

        User user = User.builder()
                .panHash(panHash)
                .fullName("Test User " + request.getUsername())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .state(request.getState())
                .ageBracket(request.getAgeBracket())
                .gender(request.getGender())
                .isPanVerified(false) // Not verified in test mode
                .isActive(true)
                .build();

        userRepository.save(user);

        return jwtService.generateToken(user);
    }
}