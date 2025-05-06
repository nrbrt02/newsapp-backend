package news.app.newsApp.repository;

import news.app.newsApp.model.Article;
import news.app.newsApp.model.Comment;
import news.app.newsApp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByArticle(Article article, Pageable pageable);
    Page<Comment> findByUser(User user, Pageable pageable);
}