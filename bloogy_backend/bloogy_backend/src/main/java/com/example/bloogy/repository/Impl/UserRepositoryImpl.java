package com.example.bloogy.repository.Impl;

import com.example.bloogy.model.User;
import com.example.bloogy.repository.UserRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of the UserRepository interface.
 * Handles Firestore operations for the "users" collection.
 */
@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Firestore firestore;

    public UserRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Saves a user to the Firestore "users" collection.
     *
     * @param user the user object to save.
     * @return the saved user object.
     */
    @Override
    public User save(User user) {
        try {
            // Save user to the "users" collection with the document ID as the user's ID
            DocumentReference docRef = firestore.collection("users").document(user.getId());
            docRef.set(user).get(); // Synchronous call to ensure operation completion
            log.info("User with ID {} saved successfully.", user.getId());
            return user;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error saving user with ID: {}", user.getId(), e);
            throw new RuntimeException("Error saving user to Firestore", e);
        }
    }

    /**
     * Retrieves a user by their ID from the Firestore "users" collection.
     *
     * @param userId the ID of the user to retrieve.
     * @return an Optional containing the user object if found, or empty if not found.
     */
    @Override
    public Optional<User> findById(String userId) {
        try {
            log.info("Fetching user with ID: {}", userId);

            // Retrieve the user document from Firestore
            DocumentReference docRef = firestore.collection("users").document(userId);
            DocumentSnapshot snapshot = docRef.get().get();

            if (snapshot.exists()) {
                log.info("User with ID {} found.", userId);
                return Optional.ofNullable(snapshot.toObject(User.class)); // Convert to User object
            } else {
                log.info("User with ID {} not found.", userId);
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error fetching user with ID: {}", userId, e);
            throw new RuntimeException("Error fetching user from Firestore", e);
        }
    }
}
