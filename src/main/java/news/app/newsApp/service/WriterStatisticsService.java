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

        // Category performance - using a different approach
        Map<String, Long> categoryPerformance = new HashMap<>();
        List<Object[]> results = articleRepository.getCategoryPerformanceByAuthorAsList(currentUser);
        for (Object[] result : results) {
            String categoryName = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            categoryPerformance.put(categoryName, count);
        }
        statistics.setCategoryPerformance(categoryPerformance);

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

        // Get comment counts
        List<Object[]> commentCountsList = commentRepository.getCommentCountsByArticleAuthor(currentUser, startDate, endDate);
        Map<String, Long> commentCounts = commentCountsList.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
        performance.put("commentCounts", commentCounts);

        // Get top articles
        Map<String, Long> topArticles = articleRepository.getTopArticlesByAuthor(currentUser, startDate, endDate)
            .stream()
            .collect(Collectors.toMap(
                Article::getTitle,
                article -> Long.valueOf(article.getViews())
            ));
        performance.put("topArticles", topArticles);

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
        
        // Convert List<Object[]> to Map<String, Long> for engagement by article
        List<Object[]> engagementList = articleRepository.getEngagementByArticle(currentUser, startDate, endDate);
        Map<String, Long> engagementByArticle = engagementList.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
        engagement.put("engagementByArticle", engagementByArticle);
        
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

        // Determine if we should use the current user or null (for admin)
        User author = currentUser.getRole() == User.Role.ADMIN ? null : currentUser;

        // Get category views
        List<Object[]> viewsList = articleRepository.getCategoryViewsByAuthor(author, startDate, endDate);
        Map<String, Long> categoryViews = new HashMap<>();
        for (Object[] row : viewsList) {
            String categoryName = (String) row[0];
            Long views = ((Number) row[1]).longValue();
            categoryViews.put(categoryName, views);
        }
        performance.put("categoryViews", categoryViews);

        // Get category comments
        List<Object[]> commentsList = commentRepository.getCategoryCommentsByArticleAuthor(author, startDate, endDate);
        Map<String, Long> categoryComments = new HashMap<>();
        for (Object[] row : commentsList) {
            String categoryName = (String) row[0];
            Long comments = ((Number) row[1]).longValue();
            categoryComments.put(categoryName, comments);
        }
        performance.put("categoryComments", categoryComments);

        // Get category engagement
        List<Object[]> engagementList = articleRepository.getCategoryEngagementByAuthor(currentUser, startDate, endDate);
        Map<String, Long> categoryEngagement = engagementList.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
        performance.put("categoryEngagement", categoryEngagement);

        // Get top categories
        List<Object[]> topCategoriesList = articleRepository.getTopCategoriesByAuthor(currentUser, startDate, endDate);
        Map<String, Long> topCategories = topCategoriesList.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
        performance.put("topCategories", topCategories);

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

        // Convert List<Object[]> to Map<String, Long> for popular topics
        User author = currentUser.getRole() == User.Role.ADMIN ? null : currentUser;
        List<Object[]> topicsList = articleRepository.getPopularTopicsByAuthor(author, startDate, endDate);
        Map<String, Long> popularTopics = topicsList.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
        insights.put("popularTopics", popularTopics);

        // Convert List<Object[]> to Map<String, Long> for reader feedback
        List<Object[]> feedbackList = commentRepository.getReaderFeedbackByArticleAuthor(currentUser, startDate, endDate);
        Map<String, Long> readerFeedback = feedbackList.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
        insights.put("readerFeedback", readerFeedback);

        // Convert List<Object[]> to Map<String, Long> for engagement patterns
        List<Object[]> patternsList = articleRepository.getEngagementPatternsByAuthor(author, startDate, endDate);
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