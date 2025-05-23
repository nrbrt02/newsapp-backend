package news.app.newsApp.service;

import news.app.newsApp.dto.WriterStatisticsDto;
import news.app.newsApp.model.Article;
import news.app.newsApp.model.User;
import news.app.newsApp.repository.ArticleRepository;
import news.app.newsApp.repository.CommentRepository;
import news.app.newsApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WriterStatisticsService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public WriterStatisticsDto getWriterDashboard() {
        User currentUser = getCurrentUser();
        WriterStatisticsDto statistics = new WriterStatisticsDto();

        // Basic counts
        statistics.setTotalArticles(articleRepository.countByAuthor(currentUser));
        statistics.setTotalViews(articleRepository.sumViewsByAuthor(currentUser));
        statistics.setTotalComments(commentRepository.countByArticleAuthor(currentUser));
        statistics.setTotalLikes(commentRepository.sumLikesByArticleAuthor(currentUser));

        // Articles by status
        Map<String, Long> articlesByStatus = new HashMap<>();
        for (Article.Status status : Article.Status.values()) {
            articlesByStatus.put(status.name(), articleRepository.countByAuthorAndStatus(currentUser, status));
        }
        statistics.setArticlesByStatus(articlesByStatus);

        // Top articles
        statistics.setTopArticles(
            articleRepository.findTopArticlesByAuthor(currentUser).stream()
                .collect(Collectors.toMap(
                    Article::getTitle,
                    article -> Long.valueOf(article.getViews())
                ))
        );

        // Category performance
        statistics.setCategoryPerformance(
            articleRepository.getCategoryPerformanceByAuthor(currentUser)
        );

        return statistics;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getArticlesPerformance(LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = getCurrentUser();
        Map<String, Object> performance = new HashMap<>();
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        // Get article counts by date
        List<Object[]> articleCountsList = articleRepository.getArticleCountsByAuthor(currentUser, startDate, endDate);
        Map<String, Long> articleCounts = articleCountsList.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
        performance.put("articleCounts", articleCounts);

        // Get view counts by date
        List<Object[]> viewCountsList = articleRepository.getViewCountsByAuthor(currentUser, startDate, endDate);
        Map<String, Long> viewCounts = viewCountsList.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
        performance.put("viewCounts", viewCounts);

        performance.put("commentCounts", commentRepository.getCommentCountsByArticleAuthor(currentUser, startDate, endDate)
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> Long.valueOf(e.getValue())
            )));
        performance.put("topArticles", articleRepository.getTopArticlesByAuthor(currentUser, startDate, endDate)
            .stream()
            .collect(Collectors.toMap(
                Article::getTitle,
                article -> Long.valueOf(article.getViews())
            )));

        return performance;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getArticlesEngagement(LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = getCurrentUser();
        Map<String, Object> engagement = new HashMap<>();
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        engagement.put("dailyViews", articleRepository.getDailyViewsByAuthor(currentUser, startDate, endDate));
        engagement.put("dailyComments", commentRepository.getDailyCommentsByArticleAuthor(currentUser, startDate, endDate));
        engagement.put("engagementByArticle", articleRepository.getEngagementByArticle(currentUser, startDate, endDate));
        engagement.put("readerFeedback", commentRepository.getReaderFeedbackByArticleAuthor(currentUser, startDate, endDate));

        return engagement;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCategoriesPerformance(LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = getCurrentUser();
        Map<String, Object> performance = new HashMap<>();
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        performance.put("categoryViews", articleRepository.getCategoryViewsByAuthor(currentUser, startDate, endDate));
        performance.put("categoryComments", commentRepository.getCategoryCommentsByArticleAuthor(currentUser, startDate, endDate));
        performance.put("categoryEngagement", articleRepository.getCategoryEngagementByAuthor(currentUser, startDate, endDate));
        performance.put("topCategories", articleRepository.getTopCategoriesByAuthor(currentUser, startDate, endDate));

        return performance;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getReadersInsights(LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = getCurrentUser();
        Map<String, Object> insights = new HashMap<>();
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        // Convert List<Object[]> to Map<String, Long> for reader demographics
        List<Object[]> demographicsList = commentRepository.getReaderDemographicsByArticleAuthor(currentUser, startDate, endDate);
        Map<String, Long> readerDemographics = demographicsList.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
        insights.put("readerDemographics", readerDemographics);

        insights.put("popularTopics", articleRepository.getPopularTopicsByAuthor(currentUser, startDate, endDate));

        // Convert List<Object[]> to Map<String, Long> for reader feedback
        List<Object[]> feedbackList = commentRepository.getReaderFeedbackByArticleAuthor(currentUser, startDate, endDate);
        Map<String, Long> readerFeedback = feedbackList.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
        insights.put("readerFeedback", readerFeedback);

        // Convert List<Object[]> to Map<String, Long> for engagement patterns
        List<Object[]> patternsList = articleRepository.getEngagementPatternsByAuthor(currentUser, startDate, endDate);
        Map<String, Long> engagementPatterns = patternsList.stream()
            .collect(Collectors.toMap(
                row -> ((java.sql.Date) row[0]).toString(),
                row -> ((Number) row[1]).longValue()
            ));
        insights.put("engagementPatterns", engagementPatterns);

        return insights;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
} 