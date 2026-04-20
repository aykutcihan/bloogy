package com.example.bloogy.unit.service;

import com.example.bloogy.model.User;
import com.example.bloogy.repository.UserRepository;
import com.example.bloogy.service.UserService;
import com.google.cloud.Timestamp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuth2User oAuth2User;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetOrCreateUser_UserExists() {
        String googleSub = "123";
        User existingUser = User.builder()
                .id(googleSub).email("existing@example.com").name("Existing User").build();

        when(oAuth2User.getAttribute("sub")).thenReturn(googleSub);
        when(userRepository.findById(googleSub)).thenReturn(Optional.of(existingUser));

        User result = userService.getOrCreateUser(null, oAuth2User);

        assertEquals(existingUser, result);
        verify(userRepository, times(1)).findById(googleSub);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetOrCreateUser_UserDoesNotExist() {
        String googleSub = "123";
        String email = "new@example.com";
        String name = "New User";

        when(oAuth2User.getAttribute("sub")).thenReturn(googleSub);
        when(oAuth2User.getAttribute("email")).thenReturn(email);
        when(oAuth2User.getAttribute("name")).thenReturn(name);
        when(userRepository.findById(googleSub)).thenReturn(Optional.empty());

        User newUser = User.builder()
                .id(googleSub).email(email).name(name).role("ROLE_USER")
                .createdDate(Timestamp.now()).updatedDate(Timestamp.now()).build();

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = userService.getOrCreateUser(null, oAuth2User);

        assertEquals(newUser, result);
        verify(userRepository, times(1)).findById(googleSub);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
