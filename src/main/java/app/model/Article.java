package app.model;

import java.time.LocalDateTime;

public class Article {
    private Long id;
    private Long likeCount;
    private Long writerId;
    private String content;
    private LocalDateTime createdAt;

    public Article(Long writerId, String content) {
        this.likeCount = 0L;
        this.writerId = writerId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public Article() {}

    public Long getId() {
        return id;
    }

    public Long getLikeCount() {
        return likeCount;
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

    public Long increaseLikeCount(){
        return ++this.likeCount;
    }
}
