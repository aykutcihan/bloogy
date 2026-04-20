package com.example.bloogy.payload.mapperDTO;

import com.example.bloogy.model.Feedback;
import com.example.bloogy.payload.requestDTO.FeedbackRequest;
import com.example.bloogy.payload.responseDTO.FeedbackResponse;
import com.google.cloud.Timestamp;

/**
 * Utility class to map between Feedback, FeedbackRequest, and FeedbackResponse.
 * This helps in converting user input, database entities, and API responses.
 */
public class FeedbackMapper {

    /**
     * Converts a FeedbackRequest object to a Feedback entity.
     *
     * @param request the FeedbackRequest containing user input.
     * @return a new Feedback entity with data from the request.
     */
    public static Feedback mapRequestToFeedback(FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setArticleId(request.getArticleId()); // Sets the related article ID
        feedback.setCommenterName(request.getCommenterName()); // Sets the name of the commenter
        feedback.setComment(request.getComment()); // Sets the feedback content
        feedback.setCommentedDate(Timestamp.now()); // Automatically sets the current timestamp
        return feedback;
    }

    /**
     * Converts a Feedback entity to a FeedbackResponse object.
     *
     * @param feedback the Feedback entity from the database.
     * @return a FeedbackResponse object for API responses.
     */
    public static FeedbackResponse mapFeedbackToResponse(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(), // The unique ID of the feedback
                feedback.getArticleId(), // The related article ID
                feedback.getCommenterName(), // The name of the commenter
                feedback.getComment(), // The feedback content
                feedback.getCommentedDate() // Returns the comment date
        );
    }

    /**
     * Updates an existing Feedback entity with new data from a FeedbackRequest.
     *
     * @param request the FeedbackRequest containing updated data.
     * @param existingFeedback the Feedback entity to be updated.
     * @return the updated Feedback entity.
     */
    public static Feedback mapUpdatedFeedback(FeedbackRequest request, Feedback existingFeedback) {
        existingFeedback.setComment(request.getComment()); // Updates the feedback content
        existingFeedback.setCommenterName(request.getCommenterName()); // Updates the commenter name
        existingFeedback.setCommentedDate(Timestamp.now()); // Sets the current timestamp
        return existingFeedback;
    }
}
