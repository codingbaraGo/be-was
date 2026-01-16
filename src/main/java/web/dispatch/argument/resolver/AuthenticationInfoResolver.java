package web.dispatch.argument.resolver;

import http.request.HttpRequest;
import web.dispatch.argument.ArgumentResolver;
import web.filter.authentication.AuthenticationInfo;

public class AuthenticationInfoResolver extends ArgumentResolver<AuthenticationInfo> {
    @Override
    public AuthenticationInfo resolve(HttpRequest request) {
        return request.getAuthenticationInfo();
    }
}
