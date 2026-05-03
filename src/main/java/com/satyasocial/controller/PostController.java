package com.satyasocial.controller;

import com.satyasocial.dto.CreatePostRequest;
import com.satyasocial.dto.PostResponse;
import com.satyasocial.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody CreatePostRequest request,
            Principal principal) {
        try {
            PostResponse post = postService.createPost(
                principal.getName(), request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "post", post
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/feed")
    public ResponseEntity<?> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {
        try {
            Page<PostResponse> feed = postService.getFeed(
                principal.getName(), page, size);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "posts", feed.getContent(),
                "totalPages", feed.getTotalPages(),
                "currentPage", page
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/explore")
    public ResponseEntity<?> explore(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<PostResponse> posts = postService.getExplorePosts(page, size);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "posts", posts.getContent(),
                "totalPages", posts.getTotalPages(),
                "currentPage", page
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserPosts(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<PostResponse> posts = postService.getUserPosts(
                username, page, size);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "posts", posts.getContent(),
                "totalPages", posts.getTotalPages(),
                "currentPage", page
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long postId,
            Principal principal) {
        try {
            postService.deletePost(principal.getName(), postId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Post deleted"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}