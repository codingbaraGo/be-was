package web.filter;

import http.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import web.filter.authentication.UserRole;

public class MemberAuthorizationFilter implements ServletFilter {

    @Override
    public void runFilter(HttpRequest request, HttpResponse response, FilterChainContainer.FilterChainEngine chain) {
        if(request.getAuthenticationInfo()==null) return;
        if(request.getAuthenticationInfo().getRole().equals(UserRole.MEMBER)){
            chain.doFilter();
        } else {
            response.setStatus(HttpStatus.FOUND);
            response.setHeader("Location", "/login");
            response.setHeader("Content-Length", "0");
        }
    }
}
