package com.example.bloogy.repository;

import com.example.bloogy.model.Feedback;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository interface for managing Feedback data in Firestore.
 * Defines the contract for CRUD operations on feedbacks associated with articles.
 */
@Repository
public interface FeedbackRepository {

    /**
     * Saves a new feedback for a specific article in Firestore.
     *
     * @param articleId the ID of the article associated with the feedback.
     * @param feedback the feedback object to save.
     * @return the saved feedback object with an assigned ID.
     */
    Feedback save(String articleId, Feedback feedback);

    /**
     * Retrieves a specific feedback by its article ID and feedback ID.
     *
     * @param articleId the ID of the article.
     * @param feedbackId the ID of the feedback to retrieve.
     * @return an Optional containing the feedback object if found, or empty if not found.
     */
    Optional<Feedback> findByArticleIdAndFeedbackId(String articleId, String feedbackId);

    /**
     * Retrieves all feedbacks for a specific article.
     *
     * @param articleId the ID of the article.
     * @return a list of feedbacks associated with the specified article.
     */
    List<Feedback> findByArticleId(String articleId);

    /**
     * Updates an existing feedback for a specific article.
     *
     * @param articleId the ID of the article associated with the feedback.
     * @param feedbackId the ID of the feedback to update.
     * @param feedback the updated feedback object.
     * @return the updated feedback object.
     */
    Feedback update(String articleId, String feedbackId, Feedback feedback);
}
