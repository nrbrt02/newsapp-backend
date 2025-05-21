package news.app.newsApp.dto;

import lombok.Data;
import news.app.newsApp.model.Article;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class ArticleDto {
    private Long id;
    private String title;
    private String content;
    private String description;
    private String featuredImage;
    private Article.Status status;
    private Integer views;
    private UserDto author;
    private CategoryDto category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ArticleImageDto> images;
    private Set<TagDto> tags;
    private Integer commentCount;
}