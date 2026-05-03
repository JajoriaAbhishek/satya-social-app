package com.satyasocial.controller;

import com.satyasocial.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{username}/follow")
    public ResponseEntity<?> follow(
            @PathVariable String username,
            Principal principal) {
        try {
            String result = followService.follow(
                principal.getName(), username);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", result
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<?> unfollow(
            @PathVariable String username,
            Principal principal) {
        try {
            String result = followService.unfollow(
                principal.getName(), username);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", result
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/{username}/profile")
    public ResponseEntity<?> getProfile(
            @PathVariable String username) {
        try {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "profile", followService.getUserProfile(username)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}