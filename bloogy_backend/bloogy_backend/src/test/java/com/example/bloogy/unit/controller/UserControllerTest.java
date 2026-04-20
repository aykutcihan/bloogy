package com.example.bloogy.unit.controller;

import com.example.bloogy.controller.UserController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private OAuth2User oAuth2User;

    @InjectMocks
    private UserController userController;

    @Test
    @SuppressWarnings("unchecked")
    void testGetCurrentUser_Success() {
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(oAuth2User.getAttribute("email")).thenReturn("test@example.com");
        when(oAuth2User.getAttribute("name")).thenReturn("Test User");
        when(oAuth2User.getAuthorities()).thenReturn(authorities);
        when(oAuth2User.getName()).thenReturn("test-sub");

        ResponseEntity<Map<String, String>> response = userController.getCurrentUser(oAuth2User);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().get("email"));
        assertEquals("Test User", response.getBody().get("name"));
        assertEquals("ROLE_USER", response.getBody().get("role"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetCurrentUser_NoAuthorities_DefaultsToRoleUser() {
        when(oAuth2User.getAttribute("email")).thenReturn("user@example.com");
        when(oAuth2User.getAttribute("name")).thenReturn("Some User");
        when(oAuth2User.getAuthorities()).thenReturn(List.of());
        when(oAuth2User.getName()).thenReturn("sub-456");

        ResponseEntity<Map<String, String>> response = userController.getCurrentUser(oAuth2User);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("ROLE_USER", response.getBody().get("role"));
    }
}
