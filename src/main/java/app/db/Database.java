package app.db;

import app.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Database {
    private static Map<Long, User> users = new ConcurrentHashMap<>();
    private static AtomicLong sequentialId = new AtomicLong(0);

    public static void addUser(User user) {
        long id = sequentialId.getAndIncrement();
        user.setUserId(id);
        users.put(id, user);
    }

    public static Optional<User> findUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public static Optional<User> findUserByEmail(String email){
        return users.values().stream().filter(u -> u.getEmail().equals(email)).findAny();
    }

    public static Collection<User> findAll() {
        return users.values();
    }
}
