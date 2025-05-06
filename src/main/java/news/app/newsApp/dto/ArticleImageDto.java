package news.app.newsApp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleImageDto {
    private Long id;
    private String image;
    private String description;
    private Long articleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}