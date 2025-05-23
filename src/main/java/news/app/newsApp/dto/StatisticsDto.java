package news.app.newsApp.dto;

import lombok.Data;
import java.util.Map;

@Data
public class StatisticsDto {
    private Long totalArticles;
    private Long totalUsers;
    private Long totalWriters;
    private Long totalCategories;
    private Long totalComments;
    private Long totalViews;
    private Map<String, Long> articlesByStatus;
    private Map<String, Long> usersByRole;
    private Map<String, Long> topCategories;
    private Map<String, Long> topWriters;
    private Map<String, Long> dailyViews;
    private Map<String, Long> dailyComments;
} 