package web.filter;

import http.request.HttpRequest;
import http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessLogFilter implements ServletFilter {

    private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    public void runFilter(HttpRequest request, HttpResponse response, FilterChainContainer.FilterChainEngine chain) {
        chain.doFilter();
        log.info("rid-{}: {} {} from {}",
                request.getOrGenerateRid(),
                request.getMethod(),
                request.getPath(),
                request.getRequestAddress());
    }
}
