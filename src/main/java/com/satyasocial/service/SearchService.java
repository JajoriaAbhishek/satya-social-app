package com.satyasocial.service;

import com.satyasocial.dto.PostResponse;
import com.satyasocial.dto.UserProfileResponse;
import com.satyasocial.repository.FollowRepository;
import com.satyasocial.repository.PostRepository;
import com.satyasocial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final PostService postService;

    public List<UserProfileResponse> searchUsers(String query) {
        return userRepository
            .findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                query, query)
            .stream()
            .map(user -> UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .state(user.getState())
                .ageBracket(user.getAgeBracket())
                .gender(user.getGender())
                .isPanVerified(user.getIsPanVerified())
                .followerCount(followRepository.countByFollowing(user))
                .followingCount(followRepository.countByFollower(user))
                .build())
            .collect(Collectors.toList());
    }

    public List<PostResponse> searchPosts(String query) {
        return postRepository
            .findByContentContainingIgnoreCaseAndIsActiveTrue(query)
            .stream()
            .map(postService::mapToResponse)
            .collect(Collectors.toList());
    }
}