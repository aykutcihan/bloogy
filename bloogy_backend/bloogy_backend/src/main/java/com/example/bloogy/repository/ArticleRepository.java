package com.example.bloogy.repository;

import com.example.bloogy.model.Article;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Article data in Firestore.
 * Defines the contract for CRUD and pagination operations on the "articles" collection.
 */
@Repository
public interface ArticleRepository {

    /**
     * Saves a new article to the Firestore database.
     *
     * @param article the article object to save.
     * @return the saved article object with an assigned ID.
     */
    Article save(Article article);

    /**
     * Retrieves an article by its ID from the Firestore database.
     *
     * @param id the ID of the article to retrieve.
     * @return the article object if found, or null if not found.
     */
    Article findById(String id);

    /**
     * Updates an existing article in the Firestore database.
     *
     * @param id the ID of the article to update.
     * @param existingArticle the article object containing updated data.
     * @return the updated article object.
     */
    Article update(String id, Article existingArticle);

    /**
     * Retrieves a paginated list of articles.
     *
     * @param pageSize the number of articles to retrieve per page.
     * @param lastDocumentId the ID of the last document from the previous page (optional).
     * @return a list of articles for the specified page.
     */
    List<Article> findByPagination(int pageSize, String lastDocumentId);
}
