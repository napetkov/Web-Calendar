package com.webcalendar.repository;

import com.webcalendar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Provides methods for user authentication and registration.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by their email address.
     * 
     * @param email the email address to search for
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if a user exists with the given email address.
     * 
     * @param email the email address to check
     * @return true if a user exists with this email, false otherwise
     */
    boolean existsByEmail(String email);
}
