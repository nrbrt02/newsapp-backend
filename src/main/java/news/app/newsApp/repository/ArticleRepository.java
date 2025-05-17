package news.app.newsApp.repository;

import news.app.newsApp.model.Article;
import news.app.newsApp.model.Category;
import news.app.newsApp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findByAuthor(User author, Pageable pageable);
    Page<Article> findByCategory(Category category, Pageable pageable);
    Page<Article> findByStatus(Article.Status status, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.title LIKE %?1% OR a.content LIKE %?1% OR a.description LIKE %?1%")
    Page<Article> searchArticles(String keyword, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' ORDER BY a.views DESC")
    List<Article> findTopArticlesByViews(Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' ORDER BY a.createdAt DESC")
    Page<Article> findLatestArticles(Pageable pageable);

    @Query("SELECT a FROM Article a")
    Page<Article> findAllWithTags(Pageable pageable);
    
    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.tags WHERE a IN :articles")
    List<Article> findArticlesWithTags(List<Article> articles);
}