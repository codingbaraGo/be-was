package web.handler;

import http.HttpMethod;
import http.request.HttpRequest;
import web.response.WebHandlerResponse;

public interface WebHandler {
    HttpMethod getMethod();
    boolean checkEndpoint(HttpMethod method, String path);
    WebHandlerResponse handle(HttpRequest request);
}
