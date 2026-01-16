package app.model;

import java.time.LocalDateTime;

public class Comment {
    private Long id;
    private Long articleId;
    private Long writerId;
    private String content;
    private LocalDateTime createdAt;

    public Comment(Long articleId, Long writerId, String content) {
        this.articleId = articleId;
        this.writerId = writerId;
        this.content = content;
    }

    public Comment(){}

    public Long getId() {
        return id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public Long getWriterId() {
        return writerId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
