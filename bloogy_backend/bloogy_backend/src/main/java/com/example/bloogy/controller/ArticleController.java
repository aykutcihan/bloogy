package com.example.bloogy.controller;

import com.example.bloogy.model.Article;
import com.example.bloogy.payload.requestDTO.ArticleRequest;
import com.example.bloogy.payload.responseDTO.ArticleResponse;
import com.example.bloogy.payload.responseDTO.GenericResponse;
import com.example.bloogy.payload.responseDTO.PaginationGenResponse;
import com.example.bloogy.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST controller for managing articles.
 * Provides endpoints for creating, retrieving, updating, and paginating articles.
 */
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ArticleController {

    private final ArticleService articleService;

    /**
     * Endpoint to create a new article.
     * Requires the user to have the 'ROLE_USER' authority.
     *
     * @param articleRequest the request body containing article details.
     * @return a response containing the created article details.
     */
    @PostMapping("/save")
    //@PreAuthorize("hasAuthority('ROLE_USER')")
    public GenericResponse<ArticleResponse> save(@RequestBody @Valid ArticleRequest articleRequest,
                                                  @AuthenticationPrincipal OAuth2User principal) {
        return articleService.save(articleRequest, principal.getAttribute("name"));
    }

    /**
     * Endpoint to retrieve an article by its ID.
     *
     * @param id the ID of the article to retrieve.
     * @return a response containing the article details.
     */
    @GetMapping("/get/{id}")
    public GenericResponse<ArticleResponse> getArticleById(@PathVariable String id) {
        return articleService.getArticleById(id);
    }

    /**
     * Endpoint to update an existing article.
     *
     * @param id the ID of the article to update.
     * @param articleRequest the request body containing updated article details.
     * @return a response containing the updated article details.
     */
    @PutMapping("/update/{id}")
    public GenericResponse<Article> updateArticle(@PathVariable String id,
                                                   @RequestBody @Valid ArticleRequest articleRequest,
                                                   @AuthenticationPrincipal OAuth2User principal) {
        return articleService.updateArticle(id, articleRequest, principal.getAttribute("name"));
    }

    /**
     * Endpoint to retrieve articles with pagination.
     *
     * @param pageSize the number of articles to retrieve per page.
     * @param lastDocumentId the ID of the last document from the previous page (optional).
     * @return a paginated response containing articles.
     */
    @GetMapping("/pagination")
    public GenericResponse<PaginationGenResponse<Article>> getArticlesWithPagination(
            @RequestParam int pageSize,
            @RequestParam(required = false) String lastDocumentId) {
        return articleService.getArticlesWithPagination(pageSize, lastDocumentId);
    }
}
