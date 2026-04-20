package com.example.bloogy.payload.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO class for receiving feedback data from the user.
 * This class includes validation rules to ensure the integrity of the feedback input.
 */
@Data
@AllArgsConstructor
public class FeedbackRequest {

    /**
     * The ID of the article this feedback is related to.
     * Validation:
     * - Must not be blank.
     */
    @NotBlank(message = "Article ID is mandatory") // Ensures the article ID is provided
    private String articleId;

    /**
     * The name of the person providing the feedback.
     * Validation:
     * - Must not be blank.
     * - Maximum length: 50 characters.
     */
    @NotBlank(message = "Commenter name is mandatory") // Ensures the name is provided
    @Size(max = 50, message = "Commenter name must not exceed 50 characters")
    private String commenterName;

    /**
     * The content of the feedback message.
     * Validation:
     * - Must not be blank.
     * - Maximum length: 500 characters.
     */
    @NotBlank(message = "Comment is mandatory") // Ensures the comment is provided
    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;
}
