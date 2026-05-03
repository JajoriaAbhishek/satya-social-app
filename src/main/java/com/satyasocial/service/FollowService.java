package com.satyasocial.service;

import com.satyasocial.dto.UserProfileResponse;
import com.satyasocial.model.Follow;
import com.satyasocial.model.User;
import com.satyasocial.repository.FollowRepository;
import com.satyasocial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional  // Add this
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public String follow(String followerUsername, String followingUsername) {

        if (followerUsername.equals(followingUsername)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }

        User follower = userRepository.findByUsername(followerUsername)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User following = userRepository.findByUsername(followingUsername)
            .orElseThrow(() -> new IllegalArgumentException("User to follow not found"));

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new IllegalStateException("Already following this user");
        }

        Follow follow = Follow.builder()
            .follower(follower)
            .following(following)
            .build();

        followRepository.save(follow);

        log.info("{} followed {}", followerUsername, followingUsername);

        return followerUsername + " is now following " + followingUsername;
    }

    public String unfollow(String followerUsername, String followingUsername) {

        User follower = userRepository.findByUsername(followerUsername)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User following = userRepository.findByUsername(followingUsername)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Follow follow = followRepository
            .findByFollowerAndFollowing(follower, following)
            .orElseThrow(() -> new IllegalStateException("Not following this user"));

        followRepository.delete(follow);

        log.info("{} unfollowed {}", followerUsername, followingUsername);

        return followerUsername + " unfollowed " + followingUsername;
    }

    public UserProfileResponse getUserProfile(String username) {

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        long followerCount = followRepository.countByFollowing(user);
        long followingCount = followRepository.countByFollower(user);

        return UserProfileResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .state(user.getState())
            .ageBracket(user.getAgeBracket())
            .gender(user.getGender())
            .isPanVerified(user.getIsPanVerified())
            .followerCount(followerCount)
            .followingCount(followingCount)
            .build();
    }
}