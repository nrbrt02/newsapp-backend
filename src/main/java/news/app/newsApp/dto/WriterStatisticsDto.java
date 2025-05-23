package news.app.newsApp.dto;

import lombok.Data;
import java.util.Map;

@Data
public class WriterStatisticsDto {
    private Long totalArticles;
    private Long totalViews;
    private Long totalComments;
    private Long totalLikes;
    private Map<String, Long> articlesByStatus;
    private Map<String, Long> topArticles;
    private Map<String, Long> categoryPerformance;
    private Map<String, Long> dailyViews;
    private Map<String, Long> dailyComments;
    private Map<String, Long> readerEngagement;
} 