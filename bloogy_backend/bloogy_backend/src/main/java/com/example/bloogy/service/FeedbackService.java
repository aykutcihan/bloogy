package com.example.bloogy.service;

import com.example.bloogy.exception.DatabaseException;
import com.example.bloogy.exception.EntityNotFoundException;
import com.example.bloogy.exception.PubSubException;
import com.example.bloogy.model.Feedback;
import com.example.bloogy.payload.mapperDTO.FeedbackMapper;
import com.example.bloogy.payload.requestDTO.FeedbackRequest;
import com.example.bloogy.payload.responseDTO.FeedbackResponse;
import com.example.bloogy.payload.responseDTO.GenericResponse;
import com.example.bloogy.repository.FeedbackRepository;
import com.example.bloogy.utils.MessageUtil;
import com.example.bloogy.utils.PubSubPublisherHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing feedbacks.
 * Provides business logic for creating, retrieving, and updating feedbacks associated with articles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final PubSubPublisherHelper pubSubPublisherHelper;

    @Value("${app.features.pubsub-enabled:false}")
    private boolean pubSubEnabled;

    @Value("${app.pubsub.topic:}")
    private String pubSubTopic;

    /**
     * Creates a new feedback for a specific article and publishes a Pub/Sub message.
     *
     * @param request the request object containing feedback details.
     * @return a GenericResponse containing the created feedback details.
     */
    public GenericResponse<FeedbackResponse> createFeedback(FeedbackRequest request) {
        try {
            Feedback feedback = FeedbackMapper.mapRequestToFeedback(request);
            Feedback savedFeedback = feedbackRepository.save(request.getArticleId(), feedback);

            // Publish a Pub/Sub message
            if (pubSubEnabled && pubSubTopic != null && !pubSubTopic.isBlank()) {
                try {
                    String message = String.format("New comment on article: %s by %s",
                            savedFeedback.getArticleId(), savedFeedback.getCommenterName());
                    pubSubPublisherHelper.publishMessage(pubSubTopic, message);
                } catch (Exception e) {
                    log.error("Failed to publish message to Pub/Sub: {}", e.getMessage());
                    throw new PubSubException(MessageUtil.PUBSUB_MESSAGE_PUBLISH_FAILED);
                }
            }

            FeedbackResponse response = FeedbackMapper.mapFeedbackToResponse(savedFeedback);
            return GenericResponse.<FeedbackResponse>builder()
                    .message(MessageUtil.FEEDBACK_CREATED_SUCCESS)
                    .httpStatus(HttpStatus.CREATED)
                    .data(response)
                    .build();
        } catch (Exception e) {
            throw new DatabaseException(MessageUtil.DATABASE_ERROR);
        }
    }

    /**
     * Retrieves a specific feedback by its article ID and feedback ID.
     *
     * @param articleId the ID of the article.
     * @param feedbackId the ID of the feedback to retrieve.
     * @return a GenericResponse containing the feedback details.
     */
    public GenericResponse<FeedbackResponse> getFeedbackById(String articleId, String feedbackId) {
        log.info("Fetching feedback for article ID: {} and feedback ID: {}", articleId, feedbackId);
        try {
            Optional<Feedback> feedbackOptional = feedbackRepository.findByArticleIdAndFeedbackId(articleId, feedbackId);

            Feedback feedback = feedbackOptional.orElseThrow(() -> {
                log.warn("Feedback with ID {} for article ID {} not found.", feedbackId, articleId);
                return new EntityNotFoundException(MessageUtil.FEEDBACK_NOT_FOUND);
            });

            FeedbackResponse response = FeedbackMapper.mapFeedbackToResponse(feedback);
            return GenericResponse.<FeedbackResponse>builder()
                    .message(MessageUtil.FEEDBACK_RETRIEVED_SUCCESS)
                    .httpStatus(HttpStatus.OK)
                    .data(response)
                    .build();
        } catch (EntityNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new DatabaseException(MessageUtil.DATABASE_ERROR);
        }
    }

    /**
     * Retrieves all feedbacks associated with a specific article.
     *
     * @param articleId the ID of the article.
     * @return a GenericResponse containing a list of feedbacks.
     */
    public GenericResponse<List<FeedbackResponse>> getFeedbacksByArticleId(String articleId) {
        try {
            List<Feedback> feedbacks = feedbackRepository.findByArticleId(articleId);
            List<FeedbackResponse> responses = feedbacks.stream()
                    .map(FeedbackMapper::mapFeedbackToResponse)
                    .collect(Collectors.toList());

            return GenericResponse.<List<FeedbackResponse>>builder()
                    .message(MessageUtil.FEEDBACK_RETRIEVED_SUCCESS)
                    .httpStatus(HttpStatus.OK)
                    .data(responses)
                    .build();
        } catch (Exception e) {
            log.error("Error occurred while fetching feedbacks for article ID {}: {}", articleId, e.getMessage(), e);
            throw new DatabaseException(MessageUtil.DATABASE_ERROR);
        }
    }

    /**
     * Updates an existing feedback for a specific article.
     *
     * @param articleId the ID of the article associated with the feedback.
     * @param feedbackId the ID of the feedback to update.
     * @param request the updated feedback details.
     * @return a GenericResponse containing the updated feedback details.
     */
    public GenericResponse<FeedbackResponse> updateFeedback(String articleId, String feedbackId, FeedbackRequest request) {
        try {
            Feedback existingFeedback = feedbackRepository.findByArticleIdAndFeedbackId(articleId, feedbackId)
                    .orElseThrow(() -> new EntityNotFoundException(MessageUtil.FEEDBACK_NOT_FOUND));

            Feedback updatedFeedback = FeedbackMapper.mapUpdatedFeedback(request, existingFeedback);
            Feedback savedFeedback = feedbackRepository.update(articleId, feedbackId, updatedFeedback);

            FeedbackResponse response = FeedbackMapper.mapFeedbackToResponse(savedFeedback);
            return GenericResponse.<FeedbackResponse>builder()
                    .message(MessageUtil.FEEDBACK_UPDATED_SUCCESS)
                    .httpStatus(HttpStatus.OK)
                    .data(response)
                    .build();
        } catch (EntityNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new DatabaseException(MessageUtil.FAILED_TO_UPDATE_FEEDBACK);
        }
    }
}
