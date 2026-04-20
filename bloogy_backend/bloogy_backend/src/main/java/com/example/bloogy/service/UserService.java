package com.example.bloogy.service;

import com.example.bloogy.exception.DatabaseException;
import com.example.bloogy.model.User;
import com.example.bloogy.repository.UserRepository;
import com.google.cloud.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

/**
 * Service class for managing user data.
 * Handles creating and retrieving users from Firestore based on Google OAuth2 information.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Retrieves an existing user or creates a new one based on Google OAuth2 information.
     *
     * @param userRequest the OAuth2 user request containing user authentication details.
     * @param oAuth2User the OAuth2 user object containing user attributes.
     * @return the user object retrieved or created in Firestore.
     */
    public User getOrCreateUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        try {
            // Extract user attributes from the OAuth2 user object
            String googleSub = oAuth2User.getAttribute("sub"); // Google unique user ID
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");

            log.info("Google user info received - sub: {}, email: {}, name: {}", googleSub, email, name);

            // Check if the user already exists in Firestore
            Optional<User> existingUserOpt = userRepository.findById(googleSub);

            if (existingUserOpt.isPresent()) {
                log.info("User with ID {} already exists. Returning existing user.", googleSub);
                return existingUserOpt.get();
            } else {
                log.info("User with ID {} does not exist. Creating new user.", googleSub);

                // Create a new user if it doesn't exist
                User newUser = User.builder()
                        .id(googleSub)
                        .email(email)
                        .name(name)
                        .role("ROLE_USER") // Default role
                        .createdDate(Timestamp.now())
                        .updatedDate(Timestamp.now())
                        .build();

                User savedUser = userRepository.save(newUser);
                log.info("New user with ID {} created successfully.", googleSub);
                return savedUser;
            }
        } catch (Exception e) {
            log.error("Error occurred while processing user: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to create or retrieve user from Firestore.");
        }
    }
}
