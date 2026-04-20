package com.example.bloogy.payload.responseDTO;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents the response structure for a feedback.
 */
@Data
@AllArgsConstructor
public class FeedbackResponse {
    private String id;
    private String articleId;
    private String commenterName;
    private String comment;
    private Timestamp commentedDate;
}
