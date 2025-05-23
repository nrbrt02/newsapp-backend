package news.app.newsApp.service;

import news.app.newsApp.dto.StatisticsDto;
import news.app.newsApp.model.Article;
import news.app.newsApp.model.User;
import news.app.newsApp.model.Category;
import news.app.newsApp.repository.ArticleRepository;
import news.app.newsApp.repository.UserRepository;
import news.app.newsApp.repository.CategoryRepository;
import news.app.newsApp.repository.CommentRepository;
import news.app.newsApp.exception.QueryExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class AdminStatisticsService {
    private static final Logger logger = LoggerFactory.getLogger(AdminStatisticsService.class);

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public StatisticsDto getDashboardStatistics() {
        StatisticsDto statistics = new StatisticsDto();
        
        // Basic counts
        statistics.setTotalArticles(articleRepository.count());
        statistics.setTotalUsers(userRepository.count());
        statistics.setTotalWriters(userRepository.countByRole(User.Role.WRITER));
        statistics.setTotalCategories(categoryRepository.count());
        statistics.setTotalComments(commentRepository.count());
        statistics.setTotalViews(articleRepository.sumViews());

        // Articles by status
        Map<String, Long> articlesByStatus = new HashMap<>();
        for (Article.Status status : Article.Status.values()) {
            articlesByStatus.put(status.name(), articleRepository.countByStatus(status));
        }
        statistics.setArticlesByStatus(articlesByStatus);

        // Users by role
        Map<String, Long> usersByRole = new HashMap<>();
        for (User.Role role : User.Role.values()) {
            usersByRole.put(role.name(), userRepository.countByRole(role));
        }
        statistics.setUsersByRole(usersByRole);

        // Top categories
        statistics.setTopCategories(
            categoryRepository.findAll().stream()
                .collect(Collectors.toMap(
                    Category::getName,
                    category -> articleRepository.countByCategory(category)
                ))
        );

        // Top writers
        statistics.setTopWriters(
            userRepository.findByRole(User.Role.WRITER).stream()
                .collect(Collectors.toMap(
                    User::getUsername,
                    writer -> articleRepository.countByAuthor(writer)
                ))
        );

        return statistics;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getArticlesOverview(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> overview = new HashMap<>();
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        overview.put("totalArticles", articleRepository.countByCreatedAtBetween(startDate, endDate));
        overview.put("publishedArticles", articleRepository.countByStatusAndCreatedAtBetween(Article.Status.PUBLISHED, startDate, endDate));
        overview.put("totalViews", articleRepository.sumViewsByCreatedAtBetween(startDate, endDate));
        overview.put("totalComments", commentRepository.countByCreatedAtBetween(startDate, endDate));
        overview.put("articlesByStatus", articleRepository.countByStatusAndCreatedAtBetweenGroupByStatus(startDate, endDate));
        overview.put("articlesByCategory", articleRepository.countByCategoryAndCreatedAtBetween(startDate, endDate));

        return overview;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUsersOverview(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> overview = new HashMap<>();
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        overview.put("totalUsers", userRepository.countByCreatedAtBetween(startDate, endDate));
        overview.put("newWriters", userRepository.countByRoleAndCreatedAtBetween(User.Role.WRITER, startDate, endDate));
        overview.put("activeWriters", userRepository.countActiveWriters(startDate, endDate));
        overview.put("usersByRole", userRepository.countByRoleAndCreatedAtBetweenGroupByRole(startDate, endDate));
        overview.put("userActivity", userRepository.getUserActivityMetrics(startDate, endDate));

        return overview;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCategoriesPerformance(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> performance = new HashMap<>();
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        try {
            logger.debug("Fetching category views for period: {} to {}", startDate, endDate);
            List<Object[]> categoryViewsList = categoryRepository.getCategoryViews(startDate, endDate);
            Map<String, Long> categoryViews = categoryViewsList.stream()
                .collect(Collectors.toMap(
                    row -> (String) row[0],
                    row -> ((Number) row[1]).longValue()
                ));
            performance.put("categoryViews", categoryViews);
            performance.put("totalViews", categoryViews.values().stream().mapToLong(Long::longValue).sum());

            logger.debug("Fetching category comments for period: {} to {}", startDate, endDate);
            List<Object[]> categoryCommentsList = categoryRepository.getCategoryComments(startDate, endDate);
            Map<String, Long> categoryComments = categoryCommentsList.stream()
                .collect(Collectors.toMap(
                    row -> (String) row[0],
                    row -> ((Number) row[1]).longValue()
                ));
            performance.put("categoryComments", categoryComments);
            performance.put("totalComments", categoryComments.values().stream().mapToLong(Long::longValue).sum());

            logger.debug("Fetching category engagement for period: {} to {}", startDate, endDate);
            Map<String, Long> categoryEngagement = categoryRepository.getCategoryEngagement(startDate, endDate);
            performance.put("categoryEngagement", categoryEngagement);
            performance.put("totalEngagement", categoryEngagement.values().stream().mapToLong(Long::longValue).sum());

            logger.debug("Fetching total articles for period: {} to {}", startDate, endDate);
            performance.put("totalArticles", categoryRepository.getTotalArticles(startDate, endDate));

            logger.debug("Fetching top categories for period: {} to {}", startDate, endDate);
            performance.put("topCategories", categoryRepository.getTopCategories(startDate, endDate));

        } catch (Exception e) {
            String errorMessage = String.format("Error fetching category performance data for period %s to %s", startDate, endDate);
            logger.error(errorMessage, e);
            throw new QueryExecutionException(
                errorMessage,
                "getCategoriesPerformance",
                String.format("startDate: %s, endDate: %s", startDate, endDate),
                e
            );
        }

        return performance;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getWritersPerformance(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> performance = new HashMap<>();
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        performance.put("writerArticles", userRepository.getWriterArticleCounts(startDate, endDate));
        performance.put("writerViews", userRepository.getWriterViewCounts(startDate, endDate));
        performance.put("writerComments", userRepository.getWriterCommentCounts(startDate, endDate));
        performance.put("topWriters", userRepository.getTopWriters(startDate, endDate));

        return performance;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getEngagementMetrics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> metrics = new HashMap<>();
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        metrics.put("dailyViews", articleRepository.getDailyViews(startDate, endDate));
        metrics.put("dailyComments", commentRepository.getDailyComments(startDate, endDate));
        metrics.put("engagementByCategory", articleRepository.getCategoryViews(startDate, endDate));
        metrics.put("userEngagement", userRepository.getUserEngagementMetrics(startDate, endDate));

        return metrics;
    }
} 