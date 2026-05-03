package com.satyasocial.repository;

import com.satyasocial.model.Follow;
import com.satyasocial.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // Check if already following
    boolean existsByFollowerAndFollowing(User follower, User following);

    // Get follow relationship
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    // Count followers
    long countByFollowing(User user);

    // Count following
    long countByFollower(User user);
}