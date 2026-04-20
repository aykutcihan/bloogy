package com.example.bloogy.unit.service;

import com.example.bloogy.exception.DatabaseException;
import com.example.bloogy.exception.EntityNotFoundException;
import com.example.bloogy.model.Feedback;
import com.example.bloogy.payload.mapperDTO.FeedbackMapper;
import com.example.bloogy.payload.requestDTO.FeedbackRequest;
import com.example.bloogy.payload.responseDTO.FeedbackResponse;
import com.example.bloogy.payload.responseDTO.GenericResponse;
import com.example.bloogy.repository.FeedbackRepository;
import com.example.bloogy.service.FeedbackService;
import com.example.bloogy.utils.PubSubPublisherHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private PubSubPublisherHelper pubSubPublisherHelper;

    @InjectMocks
    private FeedbackService feedbackService;

    @Test
    void testCreateFeedbackSuccess() {
        FeedbackRequest request = new FeedbackRequest("123", "Test User", "Test Comment");
        Feedback feedback = FeedbackMapper.mapRequestToFeedback(request);
        feedback.setId("1");

        when(feedbackRepository.save(anyString(), any(Feedback.class))).thenReturn(feedback);

        GenericResponse<FeedbackResponse> response = feedbackService.createFeedback(request);

        assertEquals(HttpStatus.CREATED, response.getHttpStatus());
        assertNotNull(response.getData());
        assertEquals("1", response.getData().getId());
        assertEquals("Test User", response.getData().getCommenterName());
        verify(feedbackRepository, times(1)).save(eq("123"), any(Feedback.class));
    }

    @Test
    void testCreateFeedback_PublishesPubSubMessage() {
        ReflectionTestUtils.setField(feedbackService, "pubSubEnabled", true);
        ReflectionTestUtils.setField(feedbackService, "pubSubTopic", "test-topic");

        FeedbackRequest request = new FeedbackRequest("article-42", "Alice", "Great article!");
        Feedback feedback = FeedbackMapper.mapRequestToFeedback(request);
        feedback.setId("1");

        when(feedbackRepository.save(anyString(), any(Feedback.class))).thenReturn(feedback);

        feedbackService.createFeedback(request);

        verify(pubSubPublisherHelper, times(1))
                .publishMessage(eq("test-topic"), contains("New comment on article: article-42 by Alice"));
    }

    @Test
    void testCreateFeedbackDatabaseError() {
        FeedbackRequest request = new FeedbackRequest("123", "Test User", "Test Comment");
        when(feedbackRepository.save(anyString(), any(Feedback.class)))
                .thenThrow(new RuntimeException("Database error"));

        DatabaseException exception = assertThrows(
                DatabaseException.class,
                () -> feedbackService.createFeedback(request)
        );
        assertEquals("An error occurred while accessing the database.", exception.getMessage());
    }

    @Test
    void testGetFeedbackByIdSuccess() {
        String articleId = "123";
        String feedbackId = "1";
        Feedback feedback = new Feedback();
        feedback.setId(feedbackId);
        feedback.setArticleId(articleId);
        feedback.setComment("Test Comment");

        when(feedbackRepository.findByArticleIdAndFeedbackId(articleId, feedbackId))
                .thenReturn(Optional.of(feedback));

        GenericResponse<FeedbackResponse> response = feedbackService.getFeedbackById(articleId, feedbackId);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Test Comment", response.getData().getComment());
        verify(feedbackRepository, times(1)).findByArticleIdAndFeedbackId(articleId, feedbackId);
    }

    @Test
    void testGetFeedbackByIdNotFound() {
        when(feedbackRepository.findByArticleIdAndFeedbackId("123", "1"))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> feedbackService.getFeedbackById("123", "1")
        );
        assertEquals("Feedback not found.", exception.getMessage());
    }

    @Test
    void testGetFeedbacksByArticleId() {
        String articleId = "123";
        Feedback feedback1 = new Feedback();
        feedback1.setId("1");
        feedback1.setArticleId(articleId);
        feedback1.setCommenterName("User1");
        feedback1.setComment("Comment1");

        when(feedbackRepository.findByArticleId(articleId)).thenReturn(List.of(feedback1));

        GenericResponse<List<FeedbackResponse>> response = feedbackService.getFeedbacksByArticleId(articleId);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(1, response.getData().size());
        assertEquals("User1", response.getData().get(0).getCommenterName());
        verify(feedbackRepository, times(1)).findByArticleId(articleId);
    }

    @Test
    void testUpdateFeedbackSuccess() {
        String articleId = "123";
        String feedbackId = "1";
        FeedbackRequest request = new FeedbackRequest("123", "Updated User", "Updated Comment");

        Feedback existingFeedback = new Feedback();
        existingFeedback.setId(feedbackId);
        existingFeedback.setArticleId(articleId);
        existingFeedback.setCommenterName("Original User");
        existingFeedback.setComment("Original Comment");

        Feedback updatedFeedback = new Feedback();
        updatedFeedback.setId(feedbackId);
        updatedFeedback.setArticleId(articleId);
        updatedFeedback.setCommenterName("Updated User");
        updatedFeedback.setComment("Updated Comment");

        when(feedbackRepository.findByArticleIdAndFeedbackId(articleId, feedbackId))
                .thenReturn(Optional.of(existingFeedback));
        when(feedbackRepository.update(eq(articleId), eq(feedbackId), any(Feedback.class)))
                .thenReturn(updatedFeedback);

        GenericResponse<FeedbackResponse> response =
                feedbackService.updateFeedback(articleId, feedbackId, request);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Updated Comment", response.getData().getComment());
        assertEquals("Updated User", response.getData().getCommenterName());
        verify(feedbackRepository, times(1)).update(eq(articleId), eq(feedbackId), any(Feedback.class));
    }
}
