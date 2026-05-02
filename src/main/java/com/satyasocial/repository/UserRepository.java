package com.satyasocial.repository;

import com.satyasocial.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Check if PAN hash already registered
    // This enforces one person one account
    boolean existsByPanHash(String panHash);

    // Check if username taken
    boolean existsByUsername(String username);

    // Find user by username for login
    Optional<User> findByUsername(String username);

}