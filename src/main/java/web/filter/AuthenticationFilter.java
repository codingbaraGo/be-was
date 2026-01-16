package web.filter;

import http.request.HttpRequest;
import http.response.HttpResponse;
import web.filter.authentication.AuthenticationInfo;
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
        AuthenticationInfo authInfo;

        if (session == null) {
            authInfo = UnanimousAuthentication.of();
        } else{
            authInfo = UserAuthentication.of(
                    session.getUserId(),
                    UserRole.valueOf(session.getUserRole()));
            authInfo.addAttribute("nickname",session.getNickname());
        }
        request.setAuthenticationInfo(authInfo);
        chain.doFilter();
    }
}
