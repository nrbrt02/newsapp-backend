package news.app.newsApp.controller;

import jakarta.validation.Valid;
import news.app.newsApp.dto.ArticleDto;
import news.app.newsApp.dto.ArticleRequest;
import news.app.newsApp.model.Article;
import news.app.newsApp.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public ResponseEntity<Page<ArticleDto>> getAllArticles(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        Page<ArticleDto> articles = articleService.getAllArticles(pageable);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/published")
    public ResponseEntity<Page<ArticleDto>> getPublishedArticles(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        Page<ArticleDto> articles = articleService.getPublishedArticles(pageable);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ArticleDto> getArticleById(@PathVariable Long id) {
        ArticleDto article = articleService.getArticleById(id);
        return ResponseEntity.ok(article);
    }

    @GetMapping("/my-articles")
    @PreAuthorize("hasAnyRole('ADMIN', 'WRITER')")
    public ResponseEntity<Page<ArticleDto>> getMyArticles(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        Page<ArticleDto> articles = articleService.getArticlesByCurrentUser(pageable);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<ArticleDto>> getArticlesByAuthor(
            @PathVariable Long authorId, 
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        Page<ArticleDto> articles = articleService.getArticlesByAuthor(authorId, pageable);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ArticleDto>> getArticlesByCategory(
            @PathVariable Long categoryId, 
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        Page<ArticleDto> articles = articleService.getArticlesByCategory(categoryId, pageable);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ArticleDto>> searchArticles(
            @RequestParam String keyword, 
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        Page<ArticleDto> articles = articleService.searchArticles(keyword, pageable);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/top")
    public ResponseEntity<List<ArticleDto>> getTopArticles(@RequestParam(defaultValue = "5") int count) {
        List<ArticleDto> articles = articleService.getTopArticles(count);
        return ResponseEntity.ok(articles);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WRITER')")
    public ResponseEntity<ArticleDto> createArticle(@Valid @RequestBody ArticleRequest articleRequest) {
        ArticleDto createdArticle = articleService.createArticle(articleRequest);
        return new ResponseEntity<>(createdArticle, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @articleService.getArticleById(#id).author.username == authentication.name")
    public ResponseEntity<ArticleDto> updateArticle(
            @PathVariable Long id, 
            @Valid @RequestBody ArticleRequest articleRequest) {
        ArticleDto updatedArticle = articleService.updateArticle(id, articleRequest);
        return ResponseEntity.ok(updatedArticle);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @articleService.getArticleById(#id).author.username == authentication.name")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @articleService.getArticleById(#id).author.username == authentication.name")
    public ResponseEntity<ArticleDto> updateArticleStatus(
            @PathVariable Long id, 
            @RequestParam Article.Status status) {
        ArticleDto updatedArticle = articleService.updateArticleStatus(id, status);
        return ResponseEntity.ok(updatedArticle);
    }
}