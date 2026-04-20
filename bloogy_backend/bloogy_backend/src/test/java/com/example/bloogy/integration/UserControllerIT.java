package com.example.bloogy.integration;

import com.example.bloogy.controller.UserController;
import com.example.bloogy.repository.UserRepository;
import com.example.bloogy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@TestPropertySource(properties = {
        "app.oauth.google.client-id=test-client-id",
        "app.oauth.google.client-secret=test-client-secret",
        "app.oauth.google.redirect-uri=http://localhost/login/oauth2/code/google"
})
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void getCurrentUser_withAuth_returnsUserDetails() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                .with(oauth2Login()
                        .attributes(a -> {
                            a.put("email", "test@example.com");
                            a.put("name", "Test User");
                        })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getCurrentUser_withoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
