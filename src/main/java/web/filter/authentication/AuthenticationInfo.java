package web.filter.authentication;

import java.util.Optional;

public interface AuthenticationInfo {
    Optional<Long> getUserId();
    UserRole getRole();
}
