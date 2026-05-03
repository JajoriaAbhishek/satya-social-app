package com.satyasocial.service;

import com.satyasocial.model.Like;
import com.satyasocial.model.Post;
import com.satyasocial.model.User;
import com.satyasocial.repository.LikeRepository;
import com.satyasocial.repository.PostRepository;
import com.satyasocial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public long toggleLike(String username, Long postId) {

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (likeRepository.existsByUserAndPost(user, post)) {
            // Unlike
            Like like = likeRepository.findByUserAndPost(user, post).get();
            likeRepository.delete(like);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postRepository.save(post);
            log.info("{} unliked post {}", username, postId);
        } else {
            // Like
            Like like = Like.builder()
                .user(user)
                .post(post)
                .build();
            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
            log.info("{} liked post {}", username, postId);
        }

        return post.getLikeCount();
    }

    public boolean isLikedByUser(String username, Long postId) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return likeRepository.existsByUserAndPost(user, post);
    }
}