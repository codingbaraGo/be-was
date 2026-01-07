package web.dispatch;

import http.request.HttpRequest;
import web.handler.WebHandler;
import web.response.HandlerResponse;

public interface HandlerAdapter {
    boolean support(WebHandler handler);
    HandlerResponse handle(HttpRequest request, WebHandler handler);
}
