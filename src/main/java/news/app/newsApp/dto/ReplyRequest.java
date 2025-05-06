package news.app.newsApp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReplyRequest {
    @NotBlank(message = "Reply content is required")
    private String content;
    
    private Long commentId;
    
    private Long parentReplyId;
}