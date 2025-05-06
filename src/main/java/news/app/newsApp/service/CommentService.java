package news.app.newsApp.service;

import news.app.newsApp.dto.CommentDto;
import news.app.newsApp.dto.CommentRequest;
import news.app.newsApp.exception.ResourceNotFoundException;
import news.app.newsApp.model.Article;
import news.app.newsApp.model.Comment;
import news.app.newsApp.model.User;
import news.app.newsApp.repository.ArticleRepository;
import news.app.newsApp.repository.CommentRepository;
import news.app.newsApp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<CommentDto> getCommentsByArticle(Long articleId, Pageable pageable) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));
        
        return commentRepository.findByArticle(article, pageable)
                .map(comment -> modelMapper.map(comment, CommentDto.class));
    }

    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        
        return modelMapper.map(comment, CommentDto.class);
    }

    @Transactional
    public CommentDto createComment(CommentRequest commentRequest) {
        User currentUser = getCurrentUser();
        
        // Only logged-in users can comment
        if (currentUser == null) {
            throw new AccessDeniedException("You must be logged in to comment");
        }

        Article article = articleRepository.findById(commentRequest.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + commentRequest.getArticleId()));

        Comment comment = new Comment();
        comment.setComment(commentRequest.getComment());
        comment.setArticle(article);
        comment.setUser(currentUser);
        comment.setEmail(currentUser.getEmail());
        comment.setLikes(0);
        comment.setStatus(1); // Active

        Comment savedComment = commentRepository.save(comment);
        return modelMapper.map(savedComment, CommentDto.class);
    }

    @Transactional
    public CommentDto updateComment(Long id, CommentRequest commentRequest) {
        User currentUser = getCurrentUser();
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        // Check if the user has permission to update the comment
        if (!currentUser.getRole().equals(User.Role.ADMIN) && !comment.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this comment");
        }

        comment.setComment(commentRequest.getComment());
        Comment updatedComment = commentRepository.save(comment);
        return modelMapper.map(updatedComment, CommentDto.class);
    }

    @Transactional
    public void deleteComment(Long id) {
        User currentUser = getCurrentUser();
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        // Check if the user has permission to delete the comment
        if (!currentUser.getRole().equals(User.Role.ADMIN) && 
            !comment.getUser().getId().equals(currentUser.getId()) &&
            !comment.getArticle().getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public CommentDto likeComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        
        comment.setLikes(comment.getLikes() + 1);
        Comment updatedComment = commentRepository.save(comment);
        return modelMapper.map(updatedComment, CommentDto.class);
    }

    @Transactional
    public CommentDto updateCommentStatus(Long id, Integer status) {
        User currentUser = getCurrentUser();
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        // Only admin or article author can update comment status
        if (!currentUser.getRole().equals(User.Role.ADMIN) && 
            !comment.getArticle().getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this comment's status");
        }

        comment.setStatus(status);
        Comment updatedComment = commentRepository.save(comment);
        return modelMapper.map(updatedComment, CommentDto.class);
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
}