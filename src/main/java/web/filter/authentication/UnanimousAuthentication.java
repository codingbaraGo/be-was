package web.filter.authentication;

import java.util.Optional;

public class UnanimousAuthentication implements AuthenticationInfo {
    private final UserRole USER_ROLE = UserRole.UNANIMOUS;

    public static UnanimousAuthentication of(){
        return new UnanimousAuthentication();
    }

    @Override
    public Optional<Long> getUserId() {
        return Optional.empty();
    }

    @Override
    public UserRole getRole() {
        return this.USER_ROLE;
    }
}
