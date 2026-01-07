package web.handler;

import http.request.HttpRequest;
import web.response.HandlerResponse;

public interface DefaultHandler extends WebHandler{
    HandlerResponse handle(HttpRequest request);
}
