package news.app.newsApp.repository;

import news.app.newsApp.model.Article;
import news.app.newsApp.model.Category;
import news.app.newsApp.model.User;
import news.app.newsApp.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findByAuthor(User author, Pageable pageable);
    Page<Article> findByCategory(Category category, Pageable pageable);
    Page<Article> findByStatus(Article.Status status, Pageable pageable);
    
    @Query("SELECT DISTINCT a FROM Article a " +
           "LEFT JOIN FETCH a.author " +
           "LEFT JOIN FETCH a.category " +
           "LEFT JOIN FETCH a.tags " +
           "WHERE a.title LIKE %?1% " +
           "OR a.content LIKE %?1% " +
           "OR a.description LIKE %?1%")
    List<Article> searchArticlesWithRelationships(String keyword);

    @Query(value = "SELECT DISTINCT a FROM Article a " +
           "WHERE CAST(a.title AS string) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR CAST(a.content AS string) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR CAST(a.description AS string) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Article> searchArticles(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' ORDER BY a.views DESC")
    List<Article> findTopArticlesByViews(Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' ORDER BY a.createdAt DESC")
    Page<Article> findLatestArticles(Pageable pageable);

    @Query("SELECT DISTINCT a FROM Article a " +
           "LEFT JOIN FETCH a.author " +
           "LEFT JOIN FETCH a.category " +
           "LEFT JOIN FETCH a.tags " +
           "WHERE a.status = 'PUBLISHED' " +
           "ORDER BY a.createdAt DESC")
    List<Article> findPublishedArticlesWithCommentCount();

    @Query("SELECT a FROM Article a")
    Page<Article> findAllWithTags(Pageable pageable);
    
    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.tags WHERE a IN :articles")
    List<Article> findArticlesWithTags(List<Article> articles);

    @Query("SELECT DISTINCT a FROM Article a " +
           "LEFT JOIN FETCH a.tags " +
           "LEFT JOIN FETCH a.images " +
           "LEFT JOIN FETCH a.category " +
           "LEFT JOIN FETCH a.author " +
           "WHERE a.id = :id")
    Article findByIdWithAllRelationships(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Comment c " +
           "LEFT JOIN FETCH c.user " +
           "LEFT JOIN FETCH c.replies r " +
           "LEFT JOIN FETCH r.user " +
           "WHERE c.article.id = :articleId " +
           "ORDER BY c.createdAt DESC")
    List<Comment> findCommentsByArticleId(@Param("articleId") Long articleId);

    @Query("SELECT DISTINCT c.comment FROM Comment c WHERE c.id = :commentId")
    String getCommentContent(@Param("commentId") Long commentId);

    @Query("SELECT DISTINCT r.content FROM Reply r WHERE r.id = :replyId")
    String getReplyContent(@Param("replyId") Long replyId);

    // Statistics methods
    Long countByStatus(Article.Status status);
    Long countByCategory(Category category);
    Long countByAuthor(User author);
    Long countByAuthorAndStatus(User author, Article.Status status);

    @Query("SELECT COALESCE(SUM(DISTINCT a.views), 0) FROM Article a")
    Long sumViews();

    @Query("SELECT COALESCE(SUM(DISTINCT a.views), 0) FROM Article a WHERE a.author = :author")
    Long sumViewsByAuthor(@Param("author") User author);

    Long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    Long countByStatusAndCreatedAtBetween(Article.Status status, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(DISTINCT a.views), 0) FROM Article a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    Long sumViewsByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a.status as status, COUNT(DISTINCT a) as count FROM Article a WHERE a.createdAt BETWEEN :startDate AND :endDate GROUP BY a.status")
    Map<String, Long> countByStatusAndCreatedAtBetweenGroupByStatus(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.name as category, COUNT(DISTINCT a) as count FROM Article a JOIN a.category c WHERE a.createdAt BETWEEN :startDate AND :endDate GROUP BY c.name")
    Map<String, Long> countByCategoryAndCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DISTINCT a FROM Article a WHERE a.author = :author ORDER BY a.views DESC")
    List<Article> findTopArticlesByAuthor(@Param("author") User author);

    @Query("SELECT c.name as category, COUNT(DISTINCT a) as count FROM Article a JOIN a.category c WHERE a.author = :author GROUP BY c.name")
    Map<String, Long> getCategoryPerformanceByAuthor(@Param("author") User author);

    @Query("SELECT DATE(a.createdAt) as date, COUNT(DISTINCT a) as count FROM Article a WHERE a.author = :author AND a.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(a.createdAt)")
    Map<String, Long> getArticleCountsByAuthor(@Param("author") User author, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(a.createdAt) as date, COALESCE(SUM(DISTINCT a.views), 0) as views FROM Article a WHERE a.author = :author AND a.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(a.createdAt)")
    Map<String, Long> getViewCountsByAuthor(@Param("author") User author, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT DISTINCT ON (DATE(a.created_at)) " +
           "DATE(a.created_at) as date, " +
           "SUM(a.views) as count " +
           "FROM articles a " +
           "WHERE a.created_at BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(a.created_at)", nativeQuery = true)
    Map<String, Long> getDailyViews(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(a.createdAt) as date, COUNT(DISTINCT a) as count FROM Article a WHERE a.author = :author AND a.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(a.createdAt)")
    Map<String, Long> getDailyViewsByAuthor(@Param("author") User author, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DISTINCT a FROM Article a WHERE a.author = :author AND a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.views DESC")
    List<Article> getTopArticlesByAuthor(@Param("author") User author, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.name as category, COALESCE(SUM(DISTINCT a.views), 0) as views FROM Article a JOIN a.category c WHERE a.author = :author AND a.createdAt BETWEEN :startDate AND :endDate GROUP BY c.name")
    Map<String, Long> getCategoryViewsByAuthor(@Param("author") User author, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.name as category, COUNT(DISTINCT a) as count FROM Article a JOIN a.category c WHERE a.createdAt BETWEEN :startDate AND :endDate GROUP BY c.name")
    Map<String, Long> getCategoryViews(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.name as category, COUNT(DISTINCT a) as count FROM Article a JOIN a.category c WHERE a.createdAt BETWEEN :startDate AND :endDate GROUP BY c.name ORDER BY count DESC")
    Map<String, Long> getTopCategories(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.name as category, COUNT(DISTINCT a) as count FROM Article a JOIN a.category c WHERE a.author = :author AND a.createdAt BETWEEN :startDate AND :endDate GROUP BY c.name ORDER BY count DESC")
    Map<String, Long> getTopCategoriesByAuthor(@Param("author") User author, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a.title as article, COUNT(DISTINCT c) as comments FROM Article a LEFT JOIN a.comments c WHERE a.author = :author AND a.createdAt BETWEEN :startDate AND :endDate GROUP BY a.title")
    Map<String, Long> getEngagementByArticle(@Param("author") User author, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t.name as topic, COUNT(DISTINCT a) as count FROM Article a JOIN a.tags t WHERE a.author = :author AND a.createdAt BETWEEN :startDate AND :endDate GROUP BY t.name ORDER BY count DESC")
    Map<String, Long> getPopularTopicsByAuthor(@Param("author") User author, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(a.createdAt) as date, COUNT(DISTINCT a.id) as count FROM Article a WHERE a.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(a.createdAt)")
    Map<String, Long> getEngagementPatternsByAuthor(@Param("author") User author, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.name as category, (COALESCE(SUM(DISTINCT a.views), 0) + COUNT(DISTINCT cm)) as engagement " +
           "FROM Article a " +
           "JOIN a.category c " +
           "LEFT JOIN a.comments cm " +
           "WHERE a.author = :author " +
           "AND a.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY c.name")
    Map<String, Long> getCategoryEngagementByAuthor(@Param("author") User author, 
                                                  @Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
}