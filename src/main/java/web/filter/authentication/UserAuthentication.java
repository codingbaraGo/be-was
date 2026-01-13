package web.filter.authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserAuthentication implements AuthenticationInfo {
    private final Long userId;
    private final UserRole userRole;
    private final Map<String, Object> attributes;

    private UserAuthentication(Long userId, UserRole userRole) {
        this.userId = userId;
        this.userRole = userRole;
        this.attributes = new HashMap<>();
    }

    public static UserAuthentication of(Long userId, UserRole userRole){
        return new UserAuthentication(userId, userRole);
    }

    @Override
    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public Optional<Object> getAttribute(String key) {
        return Optional.ofNullable(attributes.get(key));
    }

    @Override
    public Optional<Long> getUserId() {
        return Optional.of(userId);
    }

    @Override
    public UserRole getRole() {
        return userRole;
    }
}
