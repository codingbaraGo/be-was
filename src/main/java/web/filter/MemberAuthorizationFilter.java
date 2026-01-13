package web.filter;

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
            response.redirectTo("/login");
        }
    }
}
