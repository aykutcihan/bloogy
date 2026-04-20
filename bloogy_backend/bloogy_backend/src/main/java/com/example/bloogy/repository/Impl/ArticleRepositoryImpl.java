package com.example.bloogy.repository.Impl;

import com.example.bloogy.model.Article;
import com.example.bloogy.repository.ArticleRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Implementation of the ArticleRepository interface.
 * Handles database operations for the "articles" collection in Firestore.
 */
@Component
class ArticleRepositoryImpl implements ArticleRepository {

    private final Firestore firestore;

    public ArticleRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Saves a new article to the Firestore "articles" collection.
     *
     * @param article the article object to be saved.
     * @return the saved article with an assigned ID, or null if an error occurs.
     */
    @Override
    public Article save(Article article) {
        DocumentReference docReference = firestore.collection("articles").document();
        article.setId(docReference.getId()); // Assign a Firestore-generated ID to the article
        ApiFuture<WriteResult> apiFuture = docReference.set(article);

        try {
            apiFuture.get(); // Ensure the operation is complete
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Log the exception
            return null; // Return null in case of an error
        }
        return article;
    }

    /**
     * Retrieves an article by its ID.
     *
     * @param id the ID of the article to be retrieved.
     * @return the article object if found, or null otherwise.
     */
    @Override
    public Article findById(String id) {
        DocumentReference docReference = firestore.collection("articles").document(id);

        try {
            ApiFuture<DocumentSnapshot> future = docReference.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document.toObject(Article.class); // Convert Firestore document to Article object
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Log the exception
        }
        return null; // Return null if the article is not found or an error occurs
    }

    /**
     * Updates an existing article in the Firestore "articles" collection.
     *
     * @param id the ID of the article to be updated.
     * @param article the updated article object.
     * @return the updated article object.
     */
    @Override
    public Article update(String id, Article article) {
        DocumentReference docReference = firestore.collection("articles").document(id);
        article.setId(id); // Ensure the article has the correct ID
        article.setUpdatedDate(Timestamp.now()); // Set the updated date to the current timestamp
        ApiFuture<WriteResult> apiFuture = docReference.set(article); // Use set to overwrite the document

        try {
            apiFuture.get(); // Ensure the update operation is complete
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Log the exception
        }
        return article;
    }

    /**
     * Retrieves articles with pagination support.
     *
     * @param pageSize the number of articles to retrieve.
     * @param lastDocumentId the ID of the last document from the previous page (optional).
     * @return a list of articles for the specified page.
     */
    @Override
    public List<Article> findByPagination(int pageSize, String lastDocumentId) {
        try {
            CollectionReference articlesRef = firestore.collection("articles");
            Query query = articlesRef.orderBy("createdDate").limit(pageSize); // Order by creation date and limit results

            // If a cursor (lastDocumentId) is provided, adjust the query to start after it
            if (lastDocumentId != null && !lastDocumentId.isEmpty()) {
                DocumentSnapshot lastDocument = articlesRef.document(lastDocumentId).get().get();
                if (lastDocument.exists()) {
                    query = query.startAfter(lastDocument); // Start after the last document
                } else {
                    throw new IllegalArgumentException("Invalid lastDocumentId: No document found with the given ID.");
                }
            }

            List<QueryDocumentSnapshot> documents = query.get().get().getDocuments();

            // Convert Firestore documents to Article objects
            return documents.stream()
                    .map(doc -> doc.toObject(Article.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error fetching paginated articles", e); // Wrap the exception in a RuntimeException
        }
    }
}
