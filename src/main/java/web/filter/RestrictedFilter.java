package web.filter;

import exception.ErrorCode;
import exception.ServiceException;
import http.request.HttpRequest;
import http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestrictedFilter implements ServletFilter{
    private static final Logger log = LoggerFactory.getLogger(RestrictedFilter.class);

    @Override
    public void runFilter(HttpRequest request, HttpResponse response, FilterChainContainer.FilterChainEngine chain) {
        log.info("rid:{} - Request to restricted path:{}", request.getRid(), request.getPath());
        throw new ServiceException(ErrorCode.FORBIDDEN);
    }
}
