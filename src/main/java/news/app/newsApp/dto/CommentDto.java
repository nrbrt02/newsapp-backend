package news.app.newsApp.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {
    private Long id;
    private String comment;
    private Integer likes;
    private Integer status;
    private Long articleId;
    private UserDto user;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReplyDto> replies;
}