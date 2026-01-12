package web.filter.authentication;

import java.util.Optional;

public class UserAuthentication implements AuthenticationInfo {
    private final Long userId;
    private final UserRole userRole;

    private UserAuthentication(Long userId, UserRole userRole) {
        this.userId = userId;
        this.userRole = userRole;
    }

    public static UserAuthentication of(Long userId, UserRole userRole){
        return new UserAuthentication(userId, userRole);
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
