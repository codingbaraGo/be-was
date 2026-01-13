package web.filter.authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UnanimousAuthentication implements AuthenticationInfo {
    private final UserRole USER_ROLE = UserRole.UNANIMOUS;

    public UnanimousAuthentication() {
        this.attributes = new HashMap<>();
    }

    private final Map<String, Object> attributes;

    public static UnanimousAuthentication of(){
        return new UnanimousAuthentication();
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
        return Optional.empty();
    }


    @Override
    public UserRole getRole() {
        return this.USER_ROLE;
    }
}
