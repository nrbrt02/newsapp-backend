package news.app.newsApp.service;

import news.app.newsApp.dto.ArticleDto;
import news.app.newsApp.dto.ArticleRequest;
import news.app.newsApp.exception.ResourceNotFoundException;
import news.app.newsApp.model.Article;
import news.app.newsApp.model.Category;
import news.app.newsApp.model.Comment;
import news.app.newsApp.model.Reply;
import news.app.newsApp.model.Tag;
import news.app.newsApp.model.User;
import news.app.newsApp.repository.ArticleRepository;
import news.app.newsApp.repository.CategoryRepository;
import news.app.newsApp.repository.TagRepository;
import news.app.newsApp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public Page<ArticleDto> getAllArticles(Pageable pageable) {
        Page<Article> articlePage = articleRepository.findAllWithTags(pageable);
        
        List<Article> articlesWithTags = articleRepository.findArticlesWithTags(articlePage.getContent());
        
        java.util.Map<Long, Article> articlesMap = articlesWithTags.stream()
                .collect(Collectors.toMap(Article::getId, article -> article));
                
        return articlePage.map(article -> {
            Article articleWithTags = articlesMap.get(article.getId());
            return modelMapper.map(articleWithTags != null ? articleWithTags : article, ArticleDto.class);
        });
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> getArticlesByCurrentUser(Pageable pageable) {
        User currentUser = getCurrentUser();
        
        Page<Article> articlePage = articleRepository.findByAuthor(currentUser, pageable);
        
        if (!articlePage.isEmpty()) {
            List<Article> articlesWithTags = articleRepository.findArticlesWithTags(articlePage.getContent());
            
            java.util.Map<Long, Article> articlesMap = articlesWithTags.stream()
                    .collect(Collectors.toMap(Article::getId, article -> article));
                    
            return articlePage.map(article -> {
                Article articleWithTags = articlesMap.get(article.getId());
                return modelMapper.map(articleWithTags != null ? articleWithTags : article, ArticleDto.class);
            });
        }
        
        return articlePage.map(article -> modelMapper.map(article, ArticleDto.class));
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> getPublishedArticles(Pageable pageable) {
        List<Article> allArticles = articleRepository.findPublishedArticlesWithCommentCount();
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allArticles.size());
        List<Article> pageContent = allArticles.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(
            pageContent.stream()
                .map(article -> {
                    ArticleDto dto = modelMapper.map(article, ArticleDto.class);
                    // Initialize comments collection and count if needed
                    if (article.getComments() != null) {
                        dto.setCommentCount(article.getComments().size());
                    } else {
                        dto.setCommentCount(0);
                    }
                    return dto;
                })
                .collect(Collectors.toList()),
            pageable,
            allArticles.size()
        );
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticleById(Long id) {
        // First fetch the article with its basic relationships
        Article article = articleRepository.findByIdWithAllRelationships(id);
        
        if (article == null) {
            throw new ResourceNotFoundException("Article not found with id: " + id);
        }
        
        // Then fetch comments separately and maintain as List
        List<Comment> comments = articleRepository.findCommentsByArticleId(id);
        
        // Load LOB data for comments and replies
        for (Comment comment : comments) {
            // Load comment content
            String commentContent = articleRepository.getCommentContent(comment.getId());
            comment.setComment(commentContent);
            
            // Load reply contents
            if (comment.getReplies() != null) {
                for (Reply reply : comment.getReplies()) {
                    String replyContent = articleRepository.getReplyContent(reply.getId());
                    reply.setContent(replyContent);
                }
            }
        }
        
        article.setComments(comments);
        
        // Increment view count in a separate transaction
        updateArticleViews(id);
        
        ArticleDto articleDto = modelMapper.map(article, ArticleDto.class);
        articleDto.setCommentCount(comments.size());
        return articleDto;
    }
    
    @Transactional
    public void updateArticleViews(Long id) {
        articleRepository.findById(id).ifPresent(article -> {
            article.setViews(article.getViews() + 1);
            articleRepository.save(article);
        });
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> getArticlesByAuthor(Long authorId, Pageable pageable) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
        
        return articleRepository.findByAuthor(author, pageable)
                .map(article -> modelMapper.map(article, ArticleDto.class));
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> getArticlesByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        
        return articleRepository.findByCategory(category, pageable)
                .map(article -> modelMapper.map(article, ArticleDto.class));
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(String keyword, Pageable pageable) {
        // Get the page of articles with proper sorting
        Page<Article> articlePage = articleRepository.searchArticles(keyword, pageable);
        
        if (articlePage.hasContent()) {
            // Fetch the same articles with their relationships
            List<Article> articlesWithRelationships = articleRepository.searchArticlesWithRelationships(keyword);
            
            // Create a map for quick lookup
            Map<Long, Article> articleMap = articlesWithRelationships.stream()
                    .collect(Collectors.toMap(Article::getId, article -> article));
            
            // Map the page content using the fully loaded articles
            return articlePage.map(article -> {
                Article fullArticle = articleMap.get(article.getId());
                return modelMapper.map(fullArticle != null ? fullArticle : article, ArticleDto.class);
            });
        }
        
        return articlePage.map(article -> modelMapper.map(article, ArticleDto.class));
    }

    @Transactional(readOnly = true)
    public List<ArticleDto> getTopArticles(int count) {
        return articleRepository.findTopArticlesByViews(Pageable.ofSize(count))
                .stream()
                .map(article -> modelMapper.map(article, ArticleDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public ArticleDto createArticle(ArticleRequest articleRequest) {
        User currentUser = getCurrentUser();
        
        if (!currentUser.getRole().equals(User.Role.ADMIN) && !currentUser.getRole().equals(User.Role.WRITER)) {
            throw new AccessDeniedException("Only administrators and writers can create articles");
        }

        Article article = new Article();
        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        article.setDescription(articleRequest.getDescription());
        article.setFeaturedImage(articleRequest.getFeaturedImage());
        article.setStatus(articleRequest.getStatus());
        article.setAuthor(currentUser);
        article.setViews(0);

        if (articleRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(articleRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + articleRequest.getCategoryId()));
            article.setCategory(category);
        }

        Article savedArticle = articleRepository.save(article);
        
        if (articleRequest.getTagIds() != null && !articleRequest.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (Long tagId : articleRequest.getTagIds()) {
                Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagId));
                tags.add(tag);
            }
            savedArticle.setTags(tags);
            savedArticle = articleRepository.save(savedArticle);
        }

        return modelMapper.map(savedArticle, ArticleDto.class);
    }

    @Transactional
    public ArticleDto updateArticle(Long id, ArticleRequest articleRequest) {
        User currentUser = getCurrentUser();
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        if (!currentUser.getRole().equals(User.Role.ADMIN) && !article.getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this article");
        }

        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        article.setDescription(articleRequest.getDescription());
        
        if (articleRequest.getFeaturedImage() != null) {
            article.setFeaturedImage(articleRequest.getFeaturedImage());
        }
        
        article.setStatus(articleRequest.getStatus());

        if (articleRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(articleRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + articleRequest.getCategoryId()));
            article.setCategory(category);
        }

        if (articleRequest.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>();
            for (Long tagId : articleRequest.getTagIds()) {
                Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagId));
                tags.add(tag);
            }
            article.setTags(tags);
        }

        Article updatedArticle = articleRepository.save(article);
        return modelMapper.map(updatedArticle, ArticleDto.class);
    }

    @Transactional
    public void deleteArticle(Long id) {
        User currentUser = getCurrentUser();
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        if (!currentUser.getRole().equals(User.Role.ADMIN) && !article.getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this article");
        }

        articleRepository.delete(article);
    }

    @Transactional
    public ArticleDto updateArticleStatus(Long id, Article.Status status) {
        User currentUser = getCurrentUser();
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        if (!currentUser.getRole().equals(User.Role.ADMIN) && !article.getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this article's status");
        }

        article.setStatus(status);
        Article updatedArticle = articleRepository.save(article);
        return modelMapper.map(updatedArticle, ArticleDto.class);
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
}