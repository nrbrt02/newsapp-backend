package news.app.newsApp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import news.app.newsApp.model.Article;

import java.util.List;

@Data
public class ArticleRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private String description;
    
    private String featuredImage;
    
    private Article.Status status = Article.Status.DRAFT;
    
    private Long categoryId;
    
    private List<Long> tagIds;
}