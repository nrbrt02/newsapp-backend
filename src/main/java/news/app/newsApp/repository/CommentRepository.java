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

import java.util.List;

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
}