package web.dispatch.adapter;

import http.request.HttpRequest;
import web.dispatch.HandlerAdapter;
import web.handler.DefaultHandler;
import web.handler.WebHandler;
import web.response.HandlerResponse;

public class DefaultHandlerAdapter implements HandlerAdapter {
    @Override
    public boolean support(WebHandler handler) {
        return handler instanceof DefaultHandler;
    }

    @Override
    public HandlerResponse handle(HttpRequest request, WebHandler handler) {
        DefaultHandler defaultHandler = (DefaultHandler) handler;
        return defaultHandler.handle(request);
    }
}
