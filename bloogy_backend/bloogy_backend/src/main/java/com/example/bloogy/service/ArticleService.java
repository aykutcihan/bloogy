package com.example.bloogy.service;

import com.example.bloogy.exception.BusinessValidationException;
import com.example.bloogy.exception.DatabaseException;
import com.example.bloogy.exception.EntityNotFoundException;
import com.example.bloogy.exception.PubSubException;
import com.example.bloogy.model.Article;
import com.example.bloogy.payload.mapperDTO.ArticleMapper;
import com.example.bloogy.payload.requestDTO.ArticleRequest;
import com.example.bloogy.payload.responseDTO.ArticleResponse;
import com.example.bloogy.payload.responseDTO.GenericResponse;
import com.example.bloogy.payload.responseDTO.PaginationGenResponse;
import com.example.bloogy.repository.ArticleRepository;
import com.example.bloogy.utils.MessageUtil;
import com.example.bloogy.utils.PubSubPublisherHelper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing articles.
 * Provides business logic for creating, retrieving, updating, and paginating articles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final PubSubTemplate pubSubTemplate;
    private final PubSubPublisherHelper pubSubPublisherHelper;

    @Value("${app.features.pubsub-enabled:false}")
    private boolean pubSubEnabled;

    @Value("${app.pubsub.topic:}")
    private String pubSubTopic;

    /**
     * Saves a new article to the database and publishes a Pub/Sub message.
     *
     * @param request the request object containing article details.
     * @return a GenericResponse containing the created article details.
     */
    public GenericResponse<ArticleResponse> save(ArticleRequest request, String author) {
        try {
            Article article = ArticleMapper.mapArticleRequestToArticle(request);
            article.setAuthor(author);

            Article savedArticle = articleRepository.save(article);

            // Publish a Pub/Sub message
            if (pubSubEnabled && pubSubTopic != null && !pubSubTopic.isBlank()) {
                try {
                    String message = String.format("New article published: %s", savedArticle.getTitle());
                    pubSubPublisherHelper.publishMessage(pubSubTopic, message);
                } catch (Exception e) {
                    log.error("Failed to publish message to Pub/Sub: {}", e.getMessage());
                    throw new PubSubException(MessageUtil.PUBSUB_MESSAGE_PUBLISH_FAILED);
                }
            }

            ArticleResponse response = ArticleMapper.mapArticleToArticleResponse(savedArticle);
            return GenericResponse.<ArticleResponse>builder()
                    .message(MessageUtil.ARTICLE_CREATED_SUCCESS)
                    .httpStatus(HttpStatus.CREATED)
                    .data(response)
                    .build();
        } catch (Exception e) {
            throw new DatabaseException(MessageUtil.DATABASE_ERROR);
        }
    }

    /**
     * Retrieves an article by its ID.
     *
     * @param id the ID of the article to retrieve.
     * @return a GenericResponse containing the article details.
     */
    public GenericResponse<ArticleResponse> getArticleById(String id) {
        try {
            Article article = articleRepository.findById(id);
            if (article == null) {
                throw new EntityNotFoundException(MessageUtil.ARTICLE_NOT_FOUND);
            }

            ArticleResponse response = ArticleMapper.mapArticleToArticleResponse(article);
            return GenericResponse.<ArticleResponse>builder()
                    .message(MessageUtil.ARTICLE_RETRIEVED_SUCCESS)
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
     * Updates an existing article by its ID.
     *
     * @param id the ID of the article to update.
     * @param articleRequest the updated article details.
     * @return a GenericResponse containing the updated article.
     */
    public GenericResponse<Article> updateArticle(String id, ArticleRequest articleRequest, String loggedInAuthor) {
        try {
            Article existingArticle = articleRepository.findById(id);
            if (existingArticle == null) {
                throw new EntityNotFoundException(MessageUtil.ARTICLE_NOT_FOUND);
            }

            if (!existingArticle.getAuthor().equals(loggedInAuthor)) {
                throw new BusinessValidationException("You can only update your own articles.");
            }

            Article updatedArticle = ArticleMapper.mapUpdatedArticle(articleRequest, existingArticle);
            Article savedArticle = articleRepository.update(id, updatedArticle);

            return GenericResponse.<Article>builder()
                    .message(MessageUtil.ARTICLE_UPDATED_SUCCESS)
                    .httpStatus(HttpStatus.OK)
                    .data(savedArticle)
                    .build();
        } catch (EntityNotFoundException ex) {
            log.warn("Article not found for update: {}", id);
            throw ex;
        } catch (BusinessValidationException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("Error occurred while updating the article: {}", e.getMessage());
            throw new DatabaseException(MessageUtil.DATABASE_UPDATE_ERROR);
        }
    }

    /**
     * Retrieves articles with pagination.
     *
     * @param pageSize the number of articles per page.
     * @param lastDocumentId the ID of the last document from the previous page (optional).
     * @return a GenericResponse containing paginated articles and the next cursor.
     */
    public GenericResponse<PaginationGenResponse<Article>> getArticlesWithPagination(int pageSize, String lastDocumentId) {
        log.info("Fetching articles with page size: {} and last document ID: {}", pageSize, lastDocumentId);

        try {
            List<Article> articles = articleRepository.findByPagination(pageSize, lastDocumentId);

            String nextCursor = articles.isEmpty() ? null : articles.get(articles.size() - 1).getId();
            log.info("Fetched {} articles. Next cursor: {}", articles.size(), nextCursor);

            PaginationGenResponse<Article> response = PaginationGenResponse.<Article>builder()
                    .data(articles)
                    .nextCursor(nextCursor)
                    .build();

            return GenericResponse.<PaginationGenResponse<Article>>builder()
                    .message(MessageUtil.ARTICLES_RETRIEVED_SUCCESS)
                    .httpStatus(HttpStatus.OK)
                    .data(response)
                    .build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid cursor: {}", e.getMessage());
            throw new DatabaseException(MessageUtil.INVALID_CURSOR);
        } catch (Exception e) {
            log.error("Error occurred while fetching articles: {}", e.getMessage());
            throw new DatabaseException(MessageUtil.DATABASE_ERROR);
        }
    }
}
