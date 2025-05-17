package news.app.newsApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "replies")
public class Reply {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    
    private Integer likes = 0;
    
    private Integer status = 1;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "parent_reply_id")
    private Long parentReplyId;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
