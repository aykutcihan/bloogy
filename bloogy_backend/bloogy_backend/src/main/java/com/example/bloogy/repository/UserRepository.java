package com.example.bloogy.repository;

import com.example.bloogy.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing User data in Firestore.
 * Defines the contract for CRUD operations on the "users" collection.
 */
@Repository
public interface UserRepository {

    /**
     * Saves a new user to the Firestore "users" collection.
     *
     * @param user the user object to save.
     * @return the saved user object.
     */
    User save(User user);

    /**
     * Retrieves a user by their ID from the Firestore "users" collection.
     *
     * @param userId the ID of the user to retrieve.
     * @return an Optional containing the user object if found, or empty if not found.
     */
    Optional<User> findById(String userId);
}
