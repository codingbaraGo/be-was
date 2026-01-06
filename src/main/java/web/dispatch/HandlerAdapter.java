package web.dispatch;

import http.request.HttpRequest;
import web.handler.WebHandler;
import web.response.WebHandlerResponse;

public interface HandlerAdapter {
    boolean support(WebHandler handler);
    WebHandlerResponse handle(HttpRequest request, WebHandler handler);
}
