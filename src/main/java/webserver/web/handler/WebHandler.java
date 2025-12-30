package webserver.web.handler;

import webserver.http.HttpMethod;
import webserver.http.request.HttpRequest;
import webserver.web.handler.response.WebHandlerResponse;

public interface WebHandler {
    HttpMethod getMethod();
    boolean checkEndpoint(HttpMethod method, String path);
    WebHandlerResponse handle(HttpRequest request);
}
