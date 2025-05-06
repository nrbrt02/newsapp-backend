package news.app.newsApp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReplyDto {
    private Long id;
    private String content;
    private Integer likes;
    private Integer status;
    private Long commentId;
    private UserDto user;
    private String email;
    private Long parentReplyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}