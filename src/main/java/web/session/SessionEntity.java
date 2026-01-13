package web.session;

public class SessionEntity {
    private final String id;      // 세션 아이디(UUID)
    private final long userId;    // DB 키
    private final String userRole;
    private final String nickname;
    private final long createdAt;
    private volatile long lastAccessAt;

    public SessionEntity(String id, long userId, String userRole, String nickname, long now) {
        this.id = id;
        this.userId = userId;
        this.userRole = userRole;
        this.nickname = nickname;
        this.createdAt = now;
        this.lastAccessAt = now;
    }

    public void touch(long now) { this.lastAccessAt = now; }

    public String getId() { return id; }
    public long getUserId() { return userId; }
    public String getUserRole() { return userRole; }
    public long getCreatedAt() { return createdAt; }
    public String getNickname() { return nickname; }

    public long getLastAccessAt() { return lastAccessAt; }
}
