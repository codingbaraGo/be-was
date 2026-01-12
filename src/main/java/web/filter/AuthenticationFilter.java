package web.filter;

import http.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import web.filter.authentication.UnanimousAuthentication;
import web.filter.authentication.UserAuthentication;
import web.filter.authentication.UserRole;
import web.session.SessionEntity;
import web.session.SessionStorage;

public class AuthenticationFilter implements ServletFilter {
    private final SessionStorage sessionManager;

    public AuthenticationFilter(SessionStorage sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void runFilter(HttpRequest request, HttpResponse response, FilterChainContainer.FilterChainEngine chain) {
        String sid = request.getCookieValue("SID").orElse(null);
        SessionEntity session = sessionManager.getValid(sid);

        if (session == null) {
            request.setAuthenticationInfo(
                    UnanimousAuthentication.of());
        } else{
            request.setAuthenticationInfo(
                    UserAuthentication.of(
                            session.getUserId(),
                            UserRole.valueOf(session.getUserRole())));
        }

        chain.doFilter();
    }
}
