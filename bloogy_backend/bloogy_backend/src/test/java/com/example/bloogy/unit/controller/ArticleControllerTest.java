package com.example.bloogy.unit.controller;

import com.example.bloogy.controller.ArticleController;
import com.example.bloogy.model.Article;
import com.example.bloogy.payload.requestDTO.ArticleRequest;
import com.example.bloogy.payload.responseDTO.ArticleResponse;
import com.example.bloogy.payload.responseDTO.GenericResponse;
import com.example.bloogy.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ArticleControllerTest {

    @Mock
    private ArticleService articleService;

    @Mock
    private OAuth2User principal;

    @InjectMocks
    private ArticleController articleController;

    @Test
    void testSaveArticle() {
        ArticleRequest request = new ArticleRequest("Test Title", "Test Content");
        ArticleResponse responseData = new ArticleResponse("123", "Test Title", "Test Content", "Test Author", null, null);
        GenericResponse<ArticleResponse> mockResponse = GenericResponse.<ArticleResponse>builder()
                .message("Created")
                .httpStatus(HttpStatus.CREATED)
                .data(responseData)
                .build();

        Mockito.when(principal.getAttribute("name")).thenReturn("Test Author");
        Mockito.when(articleService.save(any(ArticleRequest.class), eq("Test Author"))).thenReturn(mockResponse);

        GenericResponse<ArticleResponse> actualResponse = articleController.save(request, principal);

        assertEquals(HttpStatus.CREATED, actualResponse.getHttpStatus());
        assertNotNull(actualResponse.getData());
        assertEquals("Test Title", actualResponse.getData().getTitle());
        Mockito.verify(articleService, Mockito.times(1)).save(any(ArticleRequest.class), eq("Test Author"));
    }

    @Test
    void testGetArticleById() {
        String articleId = "123";
        ArticleResponse responseData = new ArticleResponse("123", "Title", "Content", "Author", null, null);
        GenericResponse<ArticleResponse> mockResponse = GenericResponse.<ArticleResponse>builder()
                .data(responseData)
                .httpStatus(HttpStatus.OK)
                .message("Found")
                .build();

        Mockito.when(articleService.getArticleById(articleId)).thenReturn(mockResponse);

        GenericResponse<ArticleResponse> actualResponse = articleController.getArticleById(articleId);

        assertEquals(HttpStatus.OK, actualResponse.getHttpStatus());
        assertEquals("Title", actualResponse.getData().getTitle());
        Mockito.verify(articleService, Mockito.times(1)).getArticleById(articleId);
    }

    @Test
    void testUpdateArticle() {
        String articleId = "123";
        ArticleRequest request = new ArticleRequest("New Title", "New Content");
        Article updated = Article.builder()
                .id(articleId)
                .title("New Title")
                .content("New Content")
                .author("Test Author")
                .build();
        GenericResponse<Article> mockResponse = GenericResponse.<Article>builder()
                .data(updated)
                .httpStatus(HttpStatus.OK)
                .message("Updated")
                .build();

        Mockito.when(principal.getAttribute("name")).thenReturn("Test Author");
        Mockito.when(articleService.updateArticle(eq(articleId), any(ArticleRequest.class), eq("Test Author")))
                .thenReturn(mockResponse);

        GenericResponse<Article> actualResponse = articleController.updateArticle(articleId, request, principal);

        assertEquals(HttpStatus.OK, actualResponse.getHttpStatus());
        assertEquals("New Title", actualResponse.getData().getTitle());
        Mockito.verify(articleService, Mockito.times(1))
                .updateArticle(eq(articleId), any(ArticleRequest.class), eq("Test Author"));
    }
}
