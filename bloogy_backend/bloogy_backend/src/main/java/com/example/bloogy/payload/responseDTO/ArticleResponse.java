package com.example.bloogy.payload.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents the response structure for an article.
 */
@Data
@AllArgsConstructor
public class ArticleResponse {

    private String id;
    private String title;
    private String content;
    private String author;
    private String createdDate;
    private String updatedDate;
}
