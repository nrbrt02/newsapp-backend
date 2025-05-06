package news.app.newsApp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TagDto {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}