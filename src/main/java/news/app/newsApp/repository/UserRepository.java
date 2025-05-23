package news.app.newsApp.repository;

import news.app.newsApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(User.Role role);

    // Statistics methods
    Long countByRole(User.Role role);
    Long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    Long countByRoleAndCreatedAtBetween(User.Role role, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT u.role as role, COUNT(DISTINCT u) as count FROM User u GROUP BY u.role")
    Map<String, Long> getUserCountsByRole();

    @Query("SELECT DATE(u.createdAt) as date, COUNT(DISTINCT u) as count FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(u.createdAt)")
    Map<String, Long> getDailyUserRegistrations(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT u.role as role, COUNT(DISTINCT u) as count FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate GROUP BY u.role")
    Map<String, Long> getUserRegistrationsByRole(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT u.role as role, COUNT(DISTINCT u) as count FROM User u WHERE u.updatedAt BETWEEN :startDate AND :endDate GROUP BY u.role")
    Map<String, Long> getActiveUsersByRole(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(u.updatedAt) as date, COUNT(DISTINCT u) as count FROM User u WHERE u.updatedAt BETWEEN :startDate AND :endDate GROUP BY DATE(u.updatedAt)")
    Map<String, Long> getDailyActiveUsers(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT u) FROM User u WHERE u.role = 'WRITER' AND u.updatedAt BETWEEN :startDate AND :endDate")
    Long countActiveWriters(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT u.role, COUNT(DISTINCT u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate GROUP BY u.role")
    List<Object[]> countByRoleAndCreatedAtBetweenGroupByRole(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(u.updatedAt), COUNT(DISTINCT u) FROM User u WHERE u.updatedAt BETWEEN :startDate AND :endDate GROUP BY DATE(u.updatedAt)")
    List<Object[]> getUserEngagementMetrics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT u.username as writer, COUNT(DISTINCT a) as count FROM User u LEFT JOIN u.articles a WHERE u.role = 'WRITER' AND a.createdAt BETWEEN :startDate AND :endDate GROUP BY u.username")
    Map<String, Long> getWriterArticleCounts(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT u.username as writer, COALESCE(SUM(a.views), 0) as views FROM User u LEFT JOIN u.articles a WHERE u.role = 'WRITER' AND a.createdAt BETWEEN :startDate AND :endDate GROUP BY u.username")
    Map<String, Long> getWriterViewCounts(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT u.username as writer, COUNT(DISTINCT c) as comments FROM User u LEFT JOIN u.comments c WHERE u.role = 'WRITER' AND c.createdAt BETWEEN :startDate AND :endDate GROUP BY u.username")
    Map<String, Long> getWriterCommentCounts(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT u.username as writer, COUNT(DISTINCT a) as articles FROM User u LEFT JOIN u.articles a WHERE u.role = 'WRITER' AND a.createdAt BETWEEN :startDate AND :endDate GROUP BY u.username ORDER BY articles DESC")
    Map<String, Long> getTopWriters(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}