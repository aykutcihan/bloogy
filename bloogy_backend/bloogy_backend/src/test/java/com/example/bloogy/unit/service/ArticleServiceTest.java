package com.example.bloogy.unit.service;

import com.example.bloogy.exception.BusinessValidationException;
import com.example.bloogy.exception.DatabaseException;
import com.example.bloogy.exception.EntityNotFoundException;
import com.example.bloogy.model.Article;
import com.example.bloogy.payload.requestDTO.ArticleRequest;
import com.example.bloogy.payload.responseDTO.ArticleResponse;
import com.example.bloogy.payload.responseDTO.GenericResponse;
import com.example.bloogy.payload.responseDTO.PaginationGenResponse;
import com.example.bloogy.repository.ArticleRepository;
import com.example.bloogy.service.ArticleService;
import com.example.bloogy.utils.PubSubPublisherHelper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private PubSubPublisherHelper pubSubPublisherHelper;

    @Mock
    private PubSubTemplate pubSubTemplate;

    @InjectMocks
    private ArticleService articleService;

    @Test
    void testSaveSuccess() {
        ArticleRequest request = new ArticleRequest("Title", "Content");
        Article article = new Article();
        article.setId("123");
        article.setTitle("Title");
        article.setAuthor("TestAuthor");

        Mockito.when(articleRepository.save(any(Article.class))).thenReturn(article);

        GenericResponse<ArticleResponse> response = articleService.save(request, "TestAuthor");

        assertEquals(HttpStatus.CREATED, response.getHttpStatus());
        assertNotNull(response.getData());
        assertEquals("123", response.getData().getId());
        Mockito.verify(articleRepository, Mockito.times(1)).save(any(Article.class));
    }

    @Test
    void testSave_PublishesPubSubMessage() {
        ReflectionTestUtils.setField(articleService, "pubSubEnabled", true);
        ReflectionTestUtils.setField(articleService, "pubSubTopic", "test-topic");

        ArticleRequest request = new ArticleRequest("PubSub Title", "Content");
        Article article = new Article();
        article.setId("123");
        article.setTitle("PubSub Title");
        article.setAuthor("TestAuthor");

        Mockito.when(articleRepository.save(any(Article.class))).thenReturn(article);

        articleService.save(request, "TestAuthor");

        Mockito.verify(pubSubPublisherHelper, Mockito.times(1))
                .publishMessage(eq("test-topic"), Mockito.contains("PubSub Title"));
    }

    @Test
    void testSaveDatabaseError() {
        ArticleRequest request = new ArticleRequest("Title", "Content");
        Mockito.when(articleRepository.save(any(Article.class)))
                .thenThrow(new RuntimeException("DB Error"));

        assertThrows(DatabaseException.class, () -> articleService.save(request, "Author"));
    }

    @Test
    void testGetArticleByIdNotFound() {
        Mockito.when(articleRepository.findById("123")).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> articleService.getArticleById("123"));
    }

    @Test
    void testUpdateArticleSuccess() {
        ArticleRequest request = new ArticleRequest("New Title", "New Content");
        Article existingArticle = Article.builder()
                .id("123").title("Old Title").content("Old Content").author("TestAuthor").build();
        Article updatedArticle = Article.builder()
                .id("123").title("New Title").content("New Content").author("TestAuthor").build();

        Mockito.when(articleRepository.findById("123")).thenReturn(existingArticle);
        Mockito.when(articleRepository.update(eq("123"), any(Article.class))).thenReturn(updatedArticle);

        GenericResponse<Article> response = articleService.updateArticle("123", request, "TestAuthor");

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("New Title", response.getData().getTitle());
        assertEquals("TestAuthor", response.getData().getAuthor());
    }

    @Test
    void testUpdateArticleNotFound() {
        ArticleRequest request = new ArticleRequest("Title", "Content");
        Mockito.when(articleRepository.findById("123")).thenReturn(null);

        assertThrows(EntityNotFoundException.class,
                () -> articleService.updateArticle("123", request, "Author"));
    }

    @Test
    void testUpdateArticle_Unauthorized() {
        ArticleRequest request = new ArticleRequest("New Title", "New Content");
        Article existingArticle = Article.builder()
                .id("123").title("Old Title").content("Old Content").author("OriginalAuthor").build();

        Mockito.when(articleRepository.findById("123")).thenReturn(existingArticle);

        BusinessValidationException ex = assertThrows(
                BusinessValidationException.class,
                () -> articleService.updateArticle("123", request, "DifferentAuthor")
        );
        assertEquals("You can only update your own articles.", ex.getMessage());
    }

    @Test
    void testGetArticlesWithPaginationSuccess() {
        List<Article> articles = new ArrayList<>();
        Article article1 = new Article();
        article1.setId("1");
        articles.add(article1);

        Mockito.when(articleRepository.findByPagination(10, null)).thenReturn(articles);

        GenericResponse<PaginationGenResponse<Article>> response =
                articleService.getArticlesWithPagination(10, null);

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getData().size());
        assertEquals("1", response.getData().getData().get(0).getId());
    }
}
