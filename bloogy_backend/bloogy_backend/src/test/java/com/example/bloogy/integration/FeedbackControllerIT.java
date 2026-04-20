package com.example.bloogy.integration;

import com.example.bloogy.controller.FeedbackController;
import com.example.bloogy.payload.responseDTO.FeedbackResponse;
import com.example.bloogy.payload.responseDTO.GenericResponse;
import com.example.bloogy.repository.UserRepository;
import com.example.bloogy.service.FeedbackService;
import com.example.bloogy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeedbackController.class)
@TestPropertySource(properties = {
        "app.oauth.google.client-id=test-client-id",
        "app.oauth.google.client-secret=test-client-secret",
        "app.oauth.google.redirect-uri=http://localhost/login/oauth2/code/google"
})
class FeedbackControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedbackService feedbackService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void createFeedback_withValidRequest_returnsOk() throws Exception {
        FeedbackResponse data = new FeedbackResponse("1", "article-1", "Alice", "Nice post!", null);
        GenericResponse<FeedbackResponse> mockResponse = GenericResponse.<FeedbackResponse>builder()
                .httpStatus(HttpStatus.CREATED).message("Created").data(data).build();

        when(feedbackService.createFeedback(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/feedbacks/save")
                .with(oauth2Login())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"articleId\":\"article-1\",\"commenterName\":\"Alice\",\"comment\":\"Nice post!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.commenterName").value("Alice"))
                .andExpect(jsonPath("$.data.comment").value("Nice post!"));
    }

    @Test
    void getFeedbackById_withAuth_returnsOk() throws Exception {
        FeedbackResponse data = new FeedbackResponse("1", "article-1", "Alice", "Nice post!", null);
        GenericResponse<FeedbackResponse> mockResponse = GenericResponse.<FeedbackResponse>builder()
                .httpStatus(HttpStatus.OK).message("Found").data(data).build();

        when(feedbackService.getFeedbackById("article-1", "1")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/feedbacks/get/article-1/1")
                .with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.comment").value("Nice post!"));
    }

    @Test
    void getFeedbacksByArticleId_withAuth_returnsList() throws Exception {
        List<FeedbackResponse> feedbacks = List.of(
                new FeedbackResponse("1", "article-1", "Alice", "Comment 1", null),
                new FeedbackResponse("2", "article-1", "Bob", "Comment 2", null)
        );
        GenericResponse<List<FeedbackResponse>> mockResponse = GenericResponse.<List<FeedbackResponse>>builder()
                .httpStatus(HttpStatus.OK).message("Found").data(feedbacks).build();

        when(feedbackService.getFeedbacksByArticleId("article-1")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/feedbacks/article/article-1")
                .with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].commenterName").value("Alice"));
    }

    @Test
    void createFeedback_withoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/feedbacks/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"articleId\":\"article-1\",\"commenterName\":\"Alice\",\"comment\":\"Nice!\"}"))
                .andExpect(status().isUnauthorized());
    }
}
