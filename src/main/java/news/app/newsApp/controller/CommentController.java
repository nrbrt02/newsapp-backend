package news.app.newsApp.controller;

import jakarta.validation.Valid;
import news.app.newsApp.dto.CommentDto;
import news.app.newsApp.dto.CommentRequest;
import news.app.newsApp.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/article/{articleId}")
    public ResponseEntity<Page<CommentDto>> getCommentsByArticle(
            @PathVariable Long articleId, 
            @PageableDefault(size = 10) Pageable pageable) {
        Page<CommentDto> comments = commentService.getCommentsByArticle(articleId, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        CommentDto comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentRequest commentRequest) {
        CommentDto createdComment = commentService.createComment(commentRequest);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @commentService.getCommentById(#id).user.username == authentication.name")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long id, 
            @Valid @RequestBody CommentRequest commentRequest) {
        CommentDto updatedComment = commentService.updateComment(id, commentRequest);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @commentService.getCommentById(#id).user.username == authentication.name or " +
                  "@commentService.getCommentById(#id).article.author.username == authentication.name")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<CommentDto> likeComment(@PathVariable Long id) {
        CommentDto likedComment = commentService.likeComment(id);
        return ResponseEntity.ok(likedComment);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @commentService.getCommentById(#id).article.author.username == authentication.name")
    public ResponseEntity<CommentDto> updateCommentStatus(
            @PathVariable Long id, 
            @RequestParam Integer status) {
        CommentDto updatedComment = commentService.updateCommentStatus(id, status);
        return ResponseEntity.ok(updatedComment);
    }
}