package news.app.newsApp.service;

import news.app.newsApp.dto.ReplyDto;
import news.app.newsApp.dto.ReplyRequest;
import news.app.newsApp.exception.ResourceNotFoundException;
import news.app.newsApp.model.Comment;
import news.app.newsApp.model.Reply;
import news.app.newsApp.model.User;
import news.app.newsApp.repository.CommentRepository;
import news.app.newsApp.repository.ReplyRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReplyService {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<ReplyDto> getRepliesByComment(Long commentId, Pageable pageable) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        
        return replyRepository.findByComment(comment, pageable)
                .map(reply -> modelMapper.map(reply, ReplyDto.class));
    }

    public List<ReplyDto> getRepliesByParentReply(Long parentReplyId) {
        return replyRepository.findByParentReplyId(parentReplyId).stream()
                .map(reply -> modelMapper.map(reply, ReplyDto.class))
                .collect(Collectors.toList());
    }

    public ReplyDto getReplyById(Long id) {
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reply not found with id: " + id));
        
        return modelMapper.map(reply, ReplyDto.class);
    }

    @Transactional
    public ReplyDto createReply(ReplyRequest replyRequest) {
        User currentUser = getCurrentUser();
        
        // Only logged-in users can reply
        if (currentUser == null) {
            throw new AccessDeniedException("You must be logged in to reply");
        }

        Comment comment = commentRepository.findById(replyRequest.getCommentId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + replyRequest.getCommentId()));

        Reply reply = new Reply();
        reply.setContent(replyRequest.getContent());
        reply.setComment(comment);
        reply.setUser(currentUser);
        reply.setEmail(currentUser.getEmail());
        reply.setLikes(0);
        reply.setStatus(1); // Active
        reply.setParentReplyId(replyRequest.getParentReplyId());

        Reply savedReply = replyRepository.save(reply);
        return modelMapper.map(savedReply, ReplyDto.class);
    }

    @Transactional
    public ReplyDto updateReply(Long id, ReplyRequest replyRequest) {
        User currentUser = getCurrentUser();
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reply not found with id: " + id));

        // Check if the user has permission to update the reply
        if (!currentUser.getRole().equals(User.Role.ADMIN) && !reply.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this reply");
        }

        reply.setContent(replyRequest.getContent());
        Reply updatedReply = replyRepository.save(reply);
        return modelMapper.map(updatedReply, ReplyDto.class);
    }

    @Transactional
    public void deleteReply(Long id) {
        User currentUser = getCurrentUser();
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reply not found with id: " + id));

        // Check if the user has permission to delete the reply
        if (!currentUser.getRole().equals(User.Role.ADMIN) && 
            !reply.getUser().getId().equals(currentUser.getId()) &&
            !reply.getComment().getArticle().getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this reply");
        }

        replyRepository.delete(reply);
    }

    @Transactional
    public ReplyDto likeReply(Long id) {
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reply not found with id: " + id));
        
        reply.setLikes(reply.getLikes() + 1);
        Reply updatedReply = replyRepository.save(reply);
        return modelMapper.map(updatedReply, ReplyDto.class);
    }

    @Transactional
    public ReplyDto updateReplyStatus(Long id, Integer status) {
        User currentUser = getCurrentUser();
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reply not found with id: " + id));

        // Only admin or article author can update reply status
        if (!currentUser.getRole().equals(User.Role.ADMIN) && 
            !reply.getComment().getArticle().getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this reply's status");
        }

        reply.setStatus(status);
        Reply updatedReply = replyRepository.save(reply);
        return modelMapper.map(updatedReply, ReplyDto.class);
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
}