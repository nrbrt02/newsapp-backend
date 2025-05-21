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

import java.util.List;

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

    @Query("SELECT DISTINCT a FROM Article a " +
           "WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
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

    @Query("SELECT c.comment FROM Comment c WHERE c.id = :commentId")
    String getCommentContent(@Param("commentId") Long commentId);

    @Query("SELECT r.content FROM Reply r WHERE r.id = :replyId")
    String getReplyContent(@Param("replyId") Long replyId);
}