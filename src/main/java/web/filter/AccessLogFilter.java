package web.filter;

import http.request.HttpRequest;
import http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.filter.authentication.UserRole;

public class AccessLogFilter implements ServletFilter {

    private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    public void runFilter(HttpRequest request, HttpResponse response, FilterChainContainer.FilterChainEngine chain) {
        request.getOrGenerateRid();
        chain.doFilter();
        if(request.getAuthenticationInfo()!=null &&
                request.getAuthenticationInfo().getRole().equals(UserRole.MEMBER)) {
            log.info("userId-{}|rid-{}: {} {} from {}",
                    request.getAuthenticationInfo().getUserId().orElse(-1L),
                    request.getOrGenerateRid(),
                    request.getMethod(),
                    request.getPath(),
                    request.getRequestAddress());
        }
        else {
            log.info("rid-{}: {} {} from {}",
                    request.getOrGenerateRid(),
                    request.getMethod(),
                    request.getPath(),
                    request.getRequestAddress());
        }
    }
}
