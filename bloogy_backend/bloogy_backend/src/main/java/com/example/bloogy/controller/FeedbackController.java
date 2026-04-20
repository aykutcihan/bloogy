package com.example.bloogy.controller;

import com.example.bloogy.payload.requestDTO.FeedbackRequest;
import com.example.bloogy.payload.responseDTO.FeedbackResponse;
import com.example.bloogy.payload.responseDTO.GenericResponse;
import com.example.bloogy.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for managing feedback.
 * Provides endpoints to create, retrieve, update, and list feedbacks.
 */
@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * Endpoint to create a new feedback.
     *
     * @param request the feedback details submitted by the user.
     * @return a response containing the created feedback details.
     */
    @PostMapping("/save")
    public GenericResponse<FeedbackResponse> createFeedback(@RequestBody @Valid FeedbackRequest request) {
        return feedbackService.createFeedback(request);
    }

    /**
     * Endpoint to retrieve a specific feedback by its article and feedback IDs.
     *
     * @param articleId the ID of the article associated with the feedback.
     * @param feedbackId the ID of the feedback to retrieve.
     * @return a response containing the feedback details.
     */
    @GetMapping("/get/{articleId}/{feedbackId}")
    public GenericResponse<FeedbackResponse> getFeedbackById(
            @PathVariable String articleId,
            @PathVariable String feedbackId) {
        return feedbackService.getFeedbackById(articleId, feedbackId);
    }

    /**
     * Endpoint to update an existing feedback.
     *
     * @param articleId the ID of the article associated with the feedback.
     * @param feedbackId the ID of the feedback to update.
     * @param request the updated feedback details.
     * @return a response containing the updated feedback details.
     */
    @PutMapping("/update/{articleId}/{feedbackId}")
    public GenericResponse<FeedbackResponse> updateFeedback(
            @PathVariable String articleId,
            @PathVariable String feedbackId,
            @RequestBody @Valid FeedbackRequest request) {
        return feedbackService.updateFeedback(articleId, feedbackId, request);
    }

    /**
     * Endpoint to retrieve all feedbacks associated with a specific article.
     *
     * @param articleId the ID of the article whose feedbacks are to be retrieved.
     * @return a response containing a list of feedbacks for the specified article.
     */
    @GetMapping("/article/{articleId}")
    public GenericResponse<List<FeedbackResponse>> getFeedbacksByArticleId(@PathVariable String articleId) {
        return feedbackService.getFeedbacksByArticleId(articleId);
    }
}
