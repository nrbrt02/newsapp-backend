package news.app.newsApp.repository;

import news.app.newsApp.model.Comment;
import news.app.newsApp.model.Reply;
import news.app.newsApp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Page<Reply> findByComment(Comment comment, Pageable pageable);
    Page<Reply> findByUser(User user, Pageable pageable);
    List<Reply> findByParentReplyId(Long parentReplyId);
}