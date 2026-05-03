package com.satyasocial.repository;

import com.satyasocial.model.Post;
import com.satyasocial.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Get posts by specific user
    Page<Post> findByUserAndIsActiveTrueOrderByCreatedAtDesc(
        User user, Pageable pageable);

    // Get feed — posts from users you follow
    @Query("""
        SELECT p FROM Post p
        WHERE p.user IN (
            SELECT f.following FROM Follow f
            WHERE f.follower = :user
        )
        AND p.isActive = true
        ORDER BY p.createdAt DESC
        """)
    Page<Post> findFeedForUser(User user, Pageable pageable);

    // Get all posts for explore page
    Page<Post> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
}