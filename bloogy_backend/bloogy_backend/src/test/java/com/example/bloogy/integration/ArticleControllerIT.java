package com.example.bloogy.integration;

import com.example.bloogy.payload.responseDTO.ArticleResponse;
import com.example.bloogy.payload.responseDTO.GenericResponse;
import com.example.bloogy.repository.UserRepository;
import com.example.bloogy.service.ArticleService;
import com.example.bloogy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.bloogy.controller.ArticleController;

@WebMvcTest(ArticleController.class)
@TestPropertySource(properties = {
        "app.oauth.google.client-id=test-client-id",
        "app.oauth.google.client-secret=test-client-secret",
        "app.oauth.google.redirect-uri=http://localhost/login/oauth2/code/google"
})
class ArticleControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void save_withValidRequestAndAuth_returnsOk() throws Exception {
        ArticleResponse data = new ArticleResponse("1", "Test Title", "Test Content", "TestUser", null, null);
        GenericResponse<ArticleResponse> mockResponse = GenericResponse.<ArticleResponse>builder()
                .httpStatus(HttpStatus.CREATED).message("Created").data(data).build();

        when(articleService.save(any(), anyString())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/articles/save")
                .with(oauth2Login().attributes(a -> a.put("name", "TestUser")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Title\",\"content\":\"Content that is long enough minimum\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.title").value("Test Title"))
                .andExpect(jsonPath("$.data.id").value("1"));
    }

    @Test
    void save_withoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/articles/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Title\",\"content\":\"Content that is long enough minimum\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getArticleById_withAuth_returnsOk() throws Exception {
        ArticleResponse data = new ArticleResponse("123", "Title", "Content", "Author", null, null);
        GenericResponse<ArticleResponse> mockResponse = GenericResponse.<ArticleResponse>builder()
                .httpStatus(HttpStatus.OK).message("Found").data(data).build();

        when(articleService.getArticleById("123")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/articles/get/123")
                .with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Title"));
    }

    @Test
    void updateArticle_withValidRequestAndAuth_returnsOk() throws Exception {
        com.example.bloogy.model.Article updated = com.example.bloogy.model.Article.builder()
                .id("123").title("Updated Title").content("Updated Content").author("TestUser").build();
        GenericResponse<com.example.bloogy.model.Article> mockResponse =
                GenericResponse.<com.example.bloogy.model.Article>builder()
                        .httpStatus(HttpStatus.OK).message("Updated").data(updated).build();

        when(articleService.updateArticle(anyString(), any(), anyString())).thenReturn(mockResponse);

        mockMvc.perform(put("/api/v1/articles/update/123")
                .with(oauth2Login().attributes(a -> a.put("name", "TestUser")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated Title\",\"content\":\"Updated content that is long enough\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated Title"));
    }
}
