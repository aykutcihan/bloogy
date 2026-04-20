package com.example.bloogy.payload.mapperDTO;

import com.example.bloogy.model.Article;
import com.example.bloogy.payload.requestDTO.ArticleRequest;
import com.example.bloogy.payload.responseDTO.ArticleResponse;
import com.google.cloud.Timestamp;

/**
 * Utility class to map between Article, ArticleRequest, and ArticleResponse.
 */
public class ArticleMapper {

    /**
     * Maps an ArticleRequest object to an Article object.
     *
     * @param request the ArticleRequest containing user input.
     * @return a new Article object with data from the request.
     */
    public static Article mapArticleRequestToArticle(ArticleRequest request) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setCreatedDate(Timestamp.now()); // Automatically sets the creation date
        return article;
    }

    /**
     * Maps an Article object to an ArticleResponse object.
     *
     * @param article the Article object from the database.
     * @return an ArticleResponse object for API responses.
     */
    public static ArticleResponse mapArticleToArticleResponse(Article article) {
        return new ArticleResponse(
                String.valueOf(article.getId()),
                article.getTitle(),
                article.getContent(),
                article.getAuthor(),
                article.getCreatedDate() != null ? article.getCreatedDate().toString() : null,
                article.getUpdatedDate() != null ? article.getUpdatedDate().toString() : null
        );
    }

    /**
     * Updates an existing Article object with new data from an ArticleRequest.
     *
     * @param request the ArticleRequest containing updated data.
     * @param existingArticle the Article object to be updated.
     * @return the updated Article object.
     */
    public static Article mapUpdatedArticle(ArticleRequest request, Article existingArticle) {
        existingArticle.setTitle(request.getTitle());
        existingArticle.setContent(request.getContent());
        existingArticle.setUpdatedDate(Timestamp.now()); // Sets the update timestamp
        return existingArticle;
    }
}
