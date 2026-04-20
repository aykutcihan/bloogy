package com.example.bloogy.utils;


public class MessageUtil {

    private MessageUtil() {
    }

    // Article ile ilgili mesajlar
    public static final String ARTICLE_CREATED_SUCCESS = "Article created successfully.";
    public static final String ARTICLE_RETRIEVED_SUCCESS = "Article retrieved successfully.";
    public static final String ARTICLE_UPDATED_SUCCESS = "Article updated successfully.";
    public static final String ARTICLES_RETRIEVED_SUCCESS = "Articles retrieved successfully.";
    public static final String ARTICLE_NOT_FOUND = "Article not found.";
    public static final String FAILED_TO_UPDATE_ARTICLE = "Failed to update the article.";

    // Feedback mesajları
    public static final String FEEDBACK_CREATED_SUCCESS = "Feedback created successfully.";
    public static final String FEEDBACK_RETRIEVED_SUCCESS = "Feedback retrieved successfully.";
    public static final String FEEDBACKS_RETRIEVED_SUCCESS = "Feedbacks retrieved successfully.";
    public static final String FEEDBACK_UPDATED_SUCCESS = "Feedback updated successfully.";
    public static final String FEEDBACK_NOT_FOUND = "Feedback not found.";
    public static final String FAILED_TO_UPDATE_FEEDBACK = "Failed to update the feedback.";

    // Genel mesajlar
    public static final String DATABASE_ERROR = "An error occurred while accessing the database.";


    // Genel hata mesajları
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred.";
    public static final String VALIDATION_ERROR = "Validation failed. Please check your input.";
    public static final String INVALID_CURSOR = "Invalid cursor provided.";


    // Pub/Sub ile ilgili mesajlar
    public static final String PUBSUB_MESSAGE_PUBLISH_FAILED = "Failed to publish message to Pub/Sub.";

    // Database hataları
    public static final String DATABASE_SAVE_ERROR = "An error occurred while saving the data.";
    public static final String DATABASE_UPDATE_ERROR = "An error occurred while updating the data.";



}
