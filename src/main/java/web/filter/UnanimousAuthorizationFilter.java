package web.filter;

import http.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import web.filter.authentication.UserRole;

public class UnanimousAuthorizationFilter implements ServletFilter {
    private final UserRole USER_ROLE = UserRole.UNANIMOUS;
    @Override
    public void runFilter(HttpRequest request, HttpResponse response, FilterChainContainer.FilterChainEngine chain) {
        if(request.getAuthenticationInfo().getRole().equals(USER_ROLE)) {
            chain.doFilter();
        } else {
            response.setStatus(HttpStatus.FOUND);
            response.setHeader("Location", "/");
            response.setHeader("Content-Length", "0");
        }
    }
}
