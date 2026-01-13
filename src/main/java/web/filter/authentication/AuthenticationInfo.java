package web.filter.authentication;

import java.util.Optional;

public interface AuthenticationInfo {
    Optional<Long> getUserId();
    Optional<Object> getAttribute(String key);
    void addAttribute(String key, Object value);
    UserRole getRole();
}
