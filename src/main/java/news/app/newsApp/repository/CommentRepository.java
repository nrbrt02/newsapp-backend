package news.app.newsApp.repository;

import news.app.newsApp.model.Article;
import news.app.newsApp.model.Comment;
import news.app.newsApp.model.User;
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
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT DISTINCT c FROM Comment c " +
           "LEFT JOIN FETCH c.replies r " +
           "LEFT JOIN FETCH c.user u " +
           "LEFT JOIN FETCH r.user ru " +
           "WHERE c.article = :article")
    List<Comment> findByArticleWithReplies(@Param("article") Article article);

    Page<Comment> findByArticle(Article article, Pageable pageable);
    Page<Comment> findByUser(User user, Pageable pageable);

    // Statistics methods
    @Query("SELECT COUNT(DISTINCT c) FROM Comment c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    Long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    Long countByUser(User user);
    Long countByUserAndCreatedAtBetween(User user, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT DATE(c.createdAt) as date, COUNT(DISTINCT c) as count FROM Comment c WHERE c.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(c.createdAt)")
    Map<String, Long> getDailyComments(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(c.createdAt) as date, COUNT(DISTINCT c) as count FROM Comment c WHERE c.user = :user AND c.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(c.createdAt)")
    Map<String, Long> getDailyCommentsByUser(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a.title as article, COUNT(DISTINCT c) as comments FROM Comment c JOIN c.article a WHERE c.user = :user AND c.createdAt BETWEEN :startDate AND :endDate GROUP BY a.title")
    Map<String, Long> getCommentsByArticle(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT cat.name as category, COUNT(DISTINCT c) as count FROM Comment c JOIN c.article a JOIN a.category cat WHERE c.user = :user AND c.createdAt BETWEEN :startDate AND :endDate GROUP BY cat.name")
    Map<String, Long> getCommentsByCategory(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT c) FROM Comment c JOIN c.article a WHERE a.author = :author")
    Long countByArticleAuthor(@Param("author") User author);

    @Query("SELECT COALESCE(SUM(c.likes), 0) FROM Comment c JOIN c.article a WHERE a.author = :author")
    Long sumLikesByArticleAuthor(@Param("author") User author);

    @Query("SELECT DATE(c.createdAt) as date, COUNT(DISTINCT c) as count FROM Comment c JOIN c.article a WHERE a.author = :author AND c.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(c.createdAt)")
    Map<String, Long> getCommentCountsByArticleAuthor(@Param("author") User author, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(c.createdAt) as date, COUNT(DISTINCT c) as count FROM Comment c JOIN c.article a WHERE a.author = :author AND c.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(c.createdAt)")
    Map<String, Long> getDailyCommentsByArticleAuthor(@Param("author") User author, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT cat.name as category, COUNT(DISTINCT c) as count FROM Comment c JOIN c.article a JOIN a.category cat WHERE a.author = :author AND c.createdAt BETWEEN :startDate AND :endDate GROUP BY cat.name")
    Map<String, Long> getCategoryCommentsByArticleAuthor(@Param("author") User author, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT u.username as reader, COUNT(DISTINCT c) as comments FROM Comment c JOIN c.article a JOIN c.user u WHERE a.author = :author AND c.createdAt BETWEEN :startDate AND :endDate GROUP BY u.username")
    Map<String, Long> getReaderDemographicsByArticleAuthor(@Param("author") User author, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a.title as article, COUNT(DISTINCT c) as comments FROM Comment c JOIN c.article a WHERE a.author = :author AND c.createdAt BETWEEN :startDate AND :endDate GROUP BY a.title")
    Map<String, Long> getReaderFeedbackByArticleAuthor(@Param("author") User author, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}