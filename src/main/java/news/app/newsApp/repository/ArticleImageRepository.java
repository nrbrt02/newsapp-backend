package news.app.newsApp.repository;

import news.app.newsApp.model.Article;
import news.app.newsApp.model.ArticleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleImageRepository extends JpaRepository<ArticleImage, Long> {
    List<ArticleImage> findByArticle(Article article);
}