package com.satyasocial.controller;

import com.satyasocial.dto.UserProfileResponse;
import com.satyasocial.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/users")
    public ResponseEntity<?> searchUsers(
            @RequestParam String q) {
        try {
            List<UserProfileResponse> users = searchService.searchUsers(q);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "users", users
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<?> searchPosts(
            @RequestParam String q) {
        try {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "posts", searchService.searchPosts(q)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}