package news.app.newsApp.repository;

import news.app.newsApp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);

    // Statistics methods
    @Query("SELECT c.name as category, COALESCE(SUM(a.views), 0) as views FROM Category c LEFT JOIN c.articles a WHERE a.createdAt BETWEEN :startDate AND :endDate GROUP BY c.name")
    List<Object[]> getCategoryViews(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Query("SELECT c.name as categoryName, COUNT(DISTINCT cm) as commentCount " +
           "FROM Category c " +
           "LEFT JOIN c.articles a " +
           "LEFT JOIN a.comments cm " +
           "WHERE cm.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY c.name")
    List<Object[]> getCategoryComments(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT c.name, (COALESCE(SUM(DISTINCT a.views), 0) + COUNT(DISTINCT cm)) as engagement FROM Category c LEFT JOIN c.articles a LEFT JOIN a.comments cm WHERE a.createdAt BETWEEN :startDate AND :endDate GROUP BY c.name")
    List<Object[]> getCategoryEngagement(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT (SELECT COUNT(DISTINCT a) FROM Article a WHERE a.createdAt BETWEEN :startDate AND :endDate) as totalArticles")
    Long getTotalArticles(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.name, COUNT(DISTINCT a) as count FROM Category c LEFT JOIN c.articles a WHERE a.createdAt BETWEEN :startDate AND :endDate GROUP BY c.name ORDER BY count DESC")
    List<Object[]> getTopCategories(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Additional methods for detailed category statistics
    @Query("SELECT c.name, COUNT(DISTINCT a) as count FROM Category c LEFT JOIN c.articles a WHERE a.createdAt BETWEEN :startDate AND :endDate GROUP BY c.name ORDER BY count DESC")
    List<Object[]> getCategoryBreakdown(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.name, COUNT(DISTINCT cm) as comments FROM Category c LEFT JOIN c.articles a LEFT JOIN a.comments cm WHERE cm.createdAt BETWEEN :startDate AND :endDate GROUP BY c.name ORDER BY comments DESC")
    List<Object[]> getCategoryCommentsBreakdown(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}