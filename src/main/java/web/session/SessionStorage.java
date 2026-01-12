package web.session;

import config.VariableConfig;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStorage {
    private final Map<String, SessionEntity> store = new ConcurrentHashMap<>();
    private final long idleMs = VariableConfig.IDLE_MS;
    private final long absoluteMs = VariableConfig.ABSOLUTE_MS;

    public SessionEntity create(long userId, String userRole) {
        long now = System.currentTimeMillis();
        String sid = UUID.randomUUID().toString();
        SessionEntity s = new SessionEntity(sid, userId, userRole, now);
        store.put(sid, s);
        return s;
    }

    public SessionEntity getValid(String sid) {
        if (sid == null || sid.isBlank()) return null;
        SessionEntity s = store.get(sid);
        if (s == null) return null;

        long now = System.currentTimeMillis();
        if (now - s.getCreatedAt() > absoluteMs || now - s.getLastAccessAt() > idleMs) {
            store.remove(sid);
            return null;
        }
        s.touch(now);
        return s;
    }

    public void invalidate(String sid) {
        if (sid != null) store.remove(sid);
    }
}
