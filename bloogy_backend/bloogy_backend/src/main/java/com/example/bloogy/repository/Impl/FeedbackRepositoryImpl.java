package com.example.bloogy.repository.Impl;

import com.example.bloogy.model.Feedback;
import com.example.bloogy.repository.FeedbackRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Implementation of the FeedbackRepository interface.
 * Handles Firestore operations for feedbacks under specific articles.
 */
@Component
public class FeedbackRepositoryImpl implements FeedbackRepository {

    private final Firestore firestore;

    public FeedbackRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Saves a new feedback under a specific article in Firestore.
     *
     * @param articleId the ID of the article associated with the feedback.
     * @param feedback the feedback object to save.
     * @return the saved feedback object with an assigned ID.
     */
    @Override
    public Feedback save(String articleId, Feedback feedback) {
        DocumentReference docRef = firestore.collection("articles")
                .document(articleId)
                .collection("feedbacks")
                .document();
        feedback.setId(docRef.getId()); // Assign a Firestore-generated ID to the feedback
        ApiFuture<WriteResult> apiFuture = docRef.set(feedback);

        try {
            apiFuture.get(); // Ensure the operation is complete
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Log the exception
        }
        return feedback;
    }

    /**
     * Retrieves a specific feedback by its article ID and feedback ID.
     *
     * @param articleId the ID of the article.
     * @param feedbackId the ID of the feedback to retrieve.
     * @return an Optional containing the feedback object, or empty if not found.
     */
    @Override
    public Optional<Feedback> findByArticleIdAndFeedbackId(String articleId, String feedbackId) {
        DocumentReference docRef = firestore.collection("articles")
                .document(articleId)
                .collection("feedbacks")
                .document(feedbackId);
        try {
            DocumentSnapshot document = docRef.get().get();
            if (document.exists()) {
                return Optional.of(document.toObject(Feedback.class)); // Convert document to Feedback object
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Log the exception
        }
        return Optional.empty(); // Return empty if feedback is not found or an error occurs
    }

    /**
     * Retrieves all feedbacks for a specific article.
     *
     * @param articleId the ID of the article.
     * @return a list of feedbacks associated with the specified article.
     */
    @Override
    public List<Feedback> findByArticleId(String articleId) {
        try {
            List<QueryDocumentSnapshot> documents = firestore.collection("articles")
                    .document(articleId)
                    .collection("feedbacks")
                    .get()
                    .get()
                    .getDocuments();

            // Convert Firestore documents to Feedback objects
            return documents.stream()
                    .map(doc -> doc.toObject(Feedback.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Log the exception
            return Collections.emptyList(); // Return an empty list if an error occurs
        }
    }

    /**
     * Updates an existing feedback for a specific article.
     *
     * @param articleId the ID of the article associated with the feedback.
     * @param feedbackId the ID of the feedback to update.
     * @param feedback the updated feedback object.
     * @return the updated feedback object.
     */
    @Override
    public Feedback update(String articleId, String feedbackId, Feedback feedback) {
        DocumentReference docRef = firestore.collection("articles")
                .document(articleId)
                .collection("feedbacks")
                .document(feedbackId);
        feedback.setId(feedbackId); // Ensure the feedback has the correct ID
        ApiFuture<WriteResult> apiFuture = docRef.set(feedback); // Use set to overwrite the document

        try {
            apiFuture.get(); // Ensure the update operation is complete
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Log the exception
        }
        return feedback;
    }
}
