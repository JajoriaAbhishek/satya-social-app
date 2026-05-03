package com.satyasocial.service;

import com.satyasocial.dto.CreatePostRequest;
import com.satyasocial.dto.PostResponse;
import com.satyasocial.model.Post;
import com.satyasocial.model.User;
import com.satyasocial.repository.PostRepository;
import com.satyasocial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponse createPost(String username, CreatePostRequest request) {

        // Validate content
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }

        if (request.getContent().length() > 280) {
            throw new IllegalArgumentException("Post cannot exceed 280 characters");
        }

        // Get user
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create post
        Post post = Post.builder()
            .user(user)
            .content(request.getContent().trim())
            .likeCount(0)
            .repostCount(0)
            .isActive(true)
            .build();

        postRepository.save(post);

        log.info("Post created by {} ", username);

        return mapToResponse(post);
    }

    public Page<PostResponse> getUserPosts(String username, int page, int size) {

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        return postRepository
            .findByUserAndIsActiveTrueOrderByCreatedAtDesc(user, pageable)
            .map(this::mapToResponse);
    }

    public Page<PostResponse> getFeed(String username, int page, int size) {

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        return postRepository
            .findFeedForUser(user, pageable)
            .map(this::mapToResponse);
    }

    public Page<PostResponse> getExplorePosts(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return postRepository
            .findByIsActiveTrueOrderByCreatedAtDesc(pageable)
            .map(this::mapToResponse);
    }

    public void deletePost(String username, Long postId) {

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("Cannot delete another user's post");
        }

        post.setIsActive(false);
        postRepository.save(post);

        log.info("Post {} deleted by {}", postId, username);
    }

    public PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
            .id(post.getId())
            .content(post.getContent())
            .username(post.getUser().getUsername())
            .fullName(post.getUser().getFullName())
            .state(post.getUser().getState())
            .ageBracket(post.getUser().getAgeBracket())
            .gender(post.getUser().getGender())
            .isPanVerified(post.getUser().getIsPanVerified())
            .likeCount(post.getLikeCount())
            .repostCount(post.getRepostCount())
            .createdAt(post.getCreatedAt())
            .build();
    }
}