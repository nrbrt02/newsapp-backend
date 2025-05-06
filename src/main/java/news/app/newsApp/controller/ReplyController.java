package news.app.newsApp.controller;

import jakarta.validation.Valid;
import news.app.newsApp.dto.ReplyDto;
import news.app.newsApp.dto.ReplyRequest;
import news.app.newsApp.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/replies")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<Page<ReplyDto>> getRepliesByComment(
            @PathVariable Long commentId, 
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ReplyDto> replies = replyService.getRepliesByComment(commentId, pageable);
        return ResponseEntity.ok(replies);
    }

    @GetMapping("/parent/{parentReplyId}")
    public ResponseEntity<List<ReplyDto>> getRepliesByParentReply(@PathVariable Long parentReplyId) {
        List<ReplyDto> replies = replyService.getRepliesByParentReply(parentReplyId);
        return ResponseEntity.ok(replies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReplyDto> getReplyById(@PathVariable Long id) {
        ReplyDto reply = replyService.getReplyById(id);
        return ResponseEntity.ok(reply);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReplyDto> createReply(@Valid @RequestBody ReplyRequest replyRequest) {
        ReplyDto createdReply = replyService.createReply(replyRequest);
        return new ResponseEntity<>(createdReply, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @replyService.getReplyById(#id).user.username == authentication.name")
    public ResponseEntity<ReplyDto> updateReply(
            @PathVariable Long id, 
            @Valid @RequestBody ReplyRequest replyRequest) {
        ReplyDto updatedReply = replyService.updateReply(id, replyRequest);
        return ResponseEntity.ok(updatedReply);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @replyService.getReplyById(#id).user.username == authentication.name or " +
                  "@replyService.getReplyById(#id).comment.article.author.username == authentication.name")
    public ResponseEntity<Void> deleteReply(@PathVariable Long id) {
        replyService.deleteReply(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ReplyDto> likeReply(@PathVariable Long id) {
        ReplyDto likedReply = replyService.likeReply(id);
        return ResponseEntity.ok(likedReply);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @replyService.getReplyById(#id).comment.article.author.username == authentication.name")
    public ResponseEntity<ReplyDto> updateReplyStatus(
            @PathVariable Long id, 
            @RequestParam Integer status) {
        ReplyDto updatedReply = replyService.updateReplyStatus(id, status);
        return ResponseEntity.ok(updatedReply);
    }
}