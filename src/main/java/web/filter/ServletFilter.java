package web.filter;

import http.request.HttpRequest;
import http.response.HttpResponse;

public interface ServletFilter {
    void runFilter(HttpRequest request, HttpResponse response, FilterChain.FilterEngine chain);
}
