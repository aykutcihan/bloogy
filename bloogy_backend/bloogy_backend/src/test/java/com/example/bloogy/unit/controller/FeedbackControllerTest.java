package com.example.bloogy.unit.controller;

import com.example.bloogy.controller.FeedbackController;
import com.example.bloogy.payload.requestDTO.FeedbackRequest;
import com.example.bloogy.payload.responseDTO.FeedbackResponse;
import com.example.bloogy.payload.responseDTO.GenericResponse;
import com.example.bloogy.service.FeedbackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackControllerTest {

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private FeedbackController feedbackController;

    @Test
    void testCreateFeedback() {
        FeedbackRequest request = new FeedbackRequest("123", "Test User", "Test Comment");
        FeedbackResponse responseData = new FeedbackResponse("1", "123", "Test User", "Test Comment", null);
        GenericResponse<FeedbackResponse> mockResponse = GenericResponse.<FeedbackResponse>builder()
                .data(responseData)
                .httpStatus(HttpStatus.CREATED)
                .message("Feedback created successfully")
                .build();

        when(feedbackService.createFeedback(any(FeedbackRequest.class))).thenReturn(mockResponse);

        GenericResponse<FeedbackResponse> actualResponse = feedbackController.createFeedback(request);

        assertEquals(HttpStatus.CREATED, actualResponse.getHttpStatus());
        assertEquals("Test User", actualResponse.getData().getCommenterName());
        verify(feedbackService, times(1)).createFeedback(any(FeedbackRequest.class));
    }

    @Test
    void testGetFeedbackById() {
        String articleId = "123";
        String feedbackId = "1";
        FeedbackResponse responseData = new FeedbackResponse("1", articleId, "User", "Comment", null);
        GenericResponse<FeedbackResponse> mockResponse = GenericResponse.<FeedbackResponse>builder()
                .data(responseData)
                .httpStatus(HttpStatus.OK)
                .message("Feedback retrieved successfully")
                .build();

        when(feedbackService.getFeedbackById(articleId, feedbackId)).thenReturn(mockResponse);

        GenericResponse<FeedbackResponse> actualResponse = feedbackController.getFeedbackById(articleId, feedbackId);

        assertEquals(HttpStatus.OK, actualResponse.getHttpStatus());
        assertEquals("Comment", actualResponse.getData().getComment());
        verify(feedbackService, times(1)).getFeedbackById(articleId, feedbackId);
    }

    @Test
    void testGetFeedbacksByArticleId() {
        String articleId = "123";
        FeedbackResponse feedbackResponse = new FeedbackResponse("1", articleId, "User", "Comment", null);
        List<FeedbackResponse> feedbackList = Collections.singletonList(feedbackResponse);
        GenericResponse<List<FeedbackResponse>> mockResponse = GenericResponse.<List<FeedbackResponse>>builder()
                .data(feedbackList)
                .httpStatus(HttpStatus.OK)
                .message("Feedbacks retrieved successfully")
                .build();

        when(feedbackService.getFeedbacksByArticleId(articleId)).thenReturn(mockResponse);

        GenericResponse<List<FeedbackResponse>> actualResponse = feedbackController.getFeedbacksByArticleId(articleId);

        assertEquals(HttpStatus.OK, actualResponse.getHttpStatus());
        assertEquals(1, actualResponse.getData().size());
        verify(feedbackService, times(1)).getFeedbacksByArticleId(articleId);
    }
}
